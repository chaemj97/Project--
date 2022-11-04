package com.ssafy.be.api.service;

import com.ssafy.be.api.dto.Creator;
import com.ssafy.be.api.dto.TotalResFont;
import com.ssafy.be.api.response.CheckFontNameRes;
import com.ssafy.be.api.response.GetFontDetailRes;
import com.ssafy.be.api.response.GetFontsRes;
import com.ssafy.be.db.entity.Font;
import com.ssafy.be.db.entity.User;
import com.ssafy.be.db.entity.UserFont;
import com.ssafy.be.db.repository.FontDownloadHistoryRepository;
import com.ssafy.be.db.repository.FontRepository;
import com.ssafy.be.db.repository.UserFontRepository;
import com.ssafy.be.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class FontServiceImpl implements FontService {
    //즐겨찾기 수 다운로드 수
    @Autowired
    FontRepository fontRepository;
    @Autowired
    UserFontRepository userFontRepository;
    @Autowired
    FontDownloadHistoryRepository fontDownloadHistoryRepository;
    @Autowired
    UserRepository userRepository;
    @Override
    public GetFontsRes getFonts(User user, Pageable pageable, String flag, String keyword) {
        Page<Font> fontAll;
        if("fontName".equals(flag)){
            fontAll = fontRepository.findByFontNameContains(pageable,keyword);
        }
        else if("creator".equals(flag)){
            List<User> creators = userRepository.findByUserNameContainsOrUserNicknameContainsIgnoreCaseOrUserLocationContains(keyword,keyword,keyword);
            fontAll = fontRepository.findByFontCreatorIn(pageable,creators);
        }
        else{

            fontAll =  fontRepository.findAll(pageable);
        }

        List<TotalResFont> resInput = new ArrayList<>();
        if(user != null){
            List<UserFont> myLike = userFontRepository.findByUser(user);
            HashSet<Long> forCheck =new HashSet<Long>();
            for(UserFont u : myLike){
                forCheck.add(u.getFont().getFontSeq());
            }
            for(Font temp : fontAll.getContent()){
                if(temp.getFontPath()==null) continue;
                TotalResFont totalResFont = TotalResFont.builder()
                        .creator(Creator.builder()
                                .email(temp.getFontCreator().getUserEmail())
                                .location(temp.getFontCreator().getUserLocation())
                                .name(temp.getFontCreator().getUserName())
                                .nickname(temp.getFontCreator().getUserNickname())
                                .build())
                        .description(temp.getFontDescription())
                        //.downloadFile(temp.getFontDownloadFile().getFileSavedPath())
                        .FontName(temp.getFontName())
                        .fontPath(temp.getFontPath())
                        .favCount(temp.getFontFavCount())
                        .fontSeq(temp.getFontSeq())
                        .downloadCount(temp.getFontDownloadCount())
                        .regDate(temp.getFontRegDate())
                        .isLike(forCheck.contains(temp.getFontSeq()))
                        .build();
                resInput.add(totalResFont);
            }
        }
        else{
            for(Font temp : fontAll.getContent()){
                if(temp.getFontPath()==null) continue;
                TotalResFont totalResFont = TotalResFont.builder()
                        .creator(Creator.builder()
                                .email(temp.getFontCreator().getUserEmail())
                                .location(temp.getFontCreator().getUserLocation())
                                .name(temp.getFontCreator().getUserName())
                                .nickname(temp.getFontCreator().getUserNickname())
                                .build())
                        .description(temp.getFontDescription())
                        //.downloadFile(temp.getFontDownloadFile().getFileSavedPath())
                        .FontName(temp.getFontName())
                        .fontPath(temp.getFontPath())
                        .favCount(temp.getFontFavCount())
                        .fontSeq(temp.getFontSeq())
                        .downloadCount(temp.getFontDownloadCount())
                        .regDate(temp.getFontRegDate())
                        .isLike(false)
                        .build();
                resInput.add(totalResFont);
            }
        }

        GetFontsRes res = GetFontsRes.builder().fonts(resInput).build();
        return res;
    }

    @Override
    public GetFontDetailRes getFont(User user, Long fontSeq) {
        //폰트 가져와
        Font target = fontRepository.findById(fontSeq).get();
        //다운로드 했는지 확인해
        boolean isDownload = fontDownloadHistoryRepository.findByUserAndDownloadFont(user, target) != null;
        //즐겨찾기 했는지 확인해
        boolean isLike = userFontRepository.findByUserAndFont(user, target) != null;
        GetFontDetailRes res = GetFontDetailRes.builder()
                .creator(Creator.builder()
                        .email(target.getFontCreator().getUserEmail())
                        .location(target.getFontCreator().getUserLocation())
                        .name(target.getFontCreator().getUserName())
                        .nickname(target.getFontCreator().getUserNickname())
                        .build())
                .description(target.getFontDescription())
                .downloadCount(target.getFontDownloadCount())
                .downloadFile(target.getFontDownloadFile().getFileSavedPath())
                .fontSeq(target.getFontSeq())
                .favCount(target.getFontFavCount())
                .fileName(target.getFontDownloadFile().getFileSavedName())
                .FontName(target.getFontName())
                .fontPath(target.getFontPath())
                .isDownload(isDownload)
                .isLike(isLike)
                .regDate(target.getFontRegDate())
                .build();

        //폰트 반환해
        return res;
    }

    @Override
    public CheckFontNameRes checkFontName(String fontName) {
        CheckFontNameRes res;
        Font findRes= fontRepository.findByFontName(fontName);
        if(findRes ==null){
            res = CheckFontNameRes.builder()
                    .isUsable(true)
                    .msg("사용가능한 폰트 이름입니다.")
                    .build();
        }
        else {
            res = CheckFontNameRes.builder()
                    .isUsable(false)
                    .msg("이미 사용중인 폰트 이름입니다.")
                    .build();
        }
        return res;
    }

    @Override
    public Long registFontInfo(String fontName, String fontDescription, User user) {
        Font font = Font.builder()
                .fontDescription(fontDescription)
                .fontName(fontName)
                .fontCreator(user)
                .build();
        if(!checkFontName(fontName).isUsable()){
            return -1L;
        }
        Font RegistedFont = fontRepository.save(font);
        return RegistedFont.getFontSeq();
    }

    @Override
    public Void createFont(List<MultipartFile> uploadImg, Long fontSeq) {
        //비동기 통신
        RestTemplate restTemplate = new RestTemplate();
        return null;
    }


}