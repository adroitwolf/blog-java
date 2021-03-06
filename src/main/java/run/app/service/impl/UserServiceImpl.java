package run.app.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import run.app.entity.DTO.BaseResponse;
import run.app.entity.DTO.UserDTO;
import run.app.entity.DTO.UserDetail;
import run.app.entity.model.*;
import run.app.entity.VO.UserParams;
import run.app.mapper.BloggerAccountMapper;
import run.app.mapper.BloggerProfileMapper;
import run.app.service.TokenService;
import run.app.service.AttachmentService;
import run.app.service.RoleService;
import run.app.service.UserService;

import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/6/26 21:34
 * Description: ://用户服务层
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    BloggerAccountMapper bloggerAccountMapper;

    @Autowired
    BloggerProfileMapper bloggerProfileMapper;

    @Autowired
    RoleService roleService;

    @Autowired
    TokenService tokenService;


    private final String TITLE = "用户头像";

    @Override
    public @NonNull
    UserDetail findUserDetailByBloggerId(@NonNull Long bloggerId) {

        BloggerAccount bloggerAccount = bloggerAccountMapper.selectByPrimaryKey(bloggerId);

        BloggerProfile bloggerProfile = bloggerProfileMapper.selectByPrimaryKey(bloggerId);

        UserDetail userDetail = new UserDetail();
        BeanUtils.copyProperties(bloggerAccount, userDetail);
        BeanUtils.copyProperties(bloggerProfile, userDetail);
        userDetail.setNickname(bloggerProfile.getNickname());
//        需要判断是否为空
        if (null != bloggerProfile.getAvatarId()) {

            userDetail.setAvatar(attachmentService.getPathById(bloggerProfile.getAvatarId()));
        }

        //找到用户权限
        userDetail.setRoles(roleService.getRolesByUserId(bloggerId).stream().map(n -> n.getAuthority()).collect(Collectors.toList()));

        return userDetail;
    }

    @Override
    @Transactional
    public @NonNull
    BaseResponse updateProfileById(@NonNull UserParams userParams, @NonNull String token) {

        BaseResponse baseResponse = new BaseResponse();

        Long userId = tokenService.getUserIdWithToken(token);
        BloggerProfile bloggerProfile = new BloggerProfile();

//        设置phone 和email  (不能更改)

//        BloggerAccount bloggerAccount = new BloggerAccount();

//        bloggerAccount.setId(userId);
//        BeanUtils.copyProperties(userParams,bloggerAccount);

//        log.info(bloggerAccount.toString());
//
//        bloggerAccountMapper.updateByPrimaryKeySelective(bloggerAccount);


//    这个才是昵称
        bloggerProfile.setBloggerId(userId);

        BeanUtils.copyProperties(userParams, bloggerProfile);
//        bloggerProfile.setNickname(userParams.getUsername());
//        bloggerProfile.setAboutMe(userParams.getAboutMe());
//        bloggerProfileMapper.updateByExampleSelective(bloggerProfileWithBLOBs,bloggerProfileExample);

        bloggerProfileMapper.updateByPrimaryKeySelective(bloggerProfile);

        UserDetail userDetail = new UserDetail();

        userDetail.setAboutMe(bloggerProfile.getAboutMe());
        userDetail.setNickname(bloggerProfile.getNickname());
//        userDetail.setEmail(bloggerAccount.getEmail());
        baseResponse.setData(userDetail);
        baseResponse.setStatus(HttpStatus.OK.value());

        return baseResponse;
    }

    @Override
    public BaseResponse getUserDetailByToken(@NonNull String token) {
        Long id = tokenService.getUserIdWithToken(token);

        return new BaseResponse(HttpStatus.OK.value(), "", findUserDetailByBloggerId(id));

    }

    @Transactional
    @Override
    public BaseResponse updateAvatar(@NonNull MultipartFile avatar, @NonNull String token) {
        Long id = tokenService.getUserIdWithToken(token);

//        删除原有附件

        BloggerProfile bloggerProfile = bloggerProfileMapper.selectByPrimaryKey(id);

        if (null != bloggerProfile.getAvatarId()) {
            attachmentService.deletePic(bloggerProfile.getAvatarId());
        }

        //        添加现有附件
        Long picId = attachmentService.uploadFile(avatar, id, TITLE);
        BloggerProfile profile = new BloggerProfile();
        profile.setBloggerId(id);
        profile.setAvatarId(picId);
        bloggerProfileMapper.updateByPrimaryKeySelective(profile);
        return new BaseResponse(HttpStatus.OK.value(), "更新头像成功", attachmentService.getPathById(picId));
    }


    @Override
    public UserDTO getUserDTOById(Long id) {
        BloggerProfile bloggerProfile = bloggerProfileMapper.selectByPrimaryKey(id);

        UserDTO user = new UserDTO();

        user.setId(bloggerProfile.getBloggerId());

        user.setNickname(bloggerProfile.getNickname());


        String avatar = null == bloggerProfile.getAvatarId() ? null : attachmentService.getPathById(bloggerProfile.getAvatarId());

        user.setAvatar(avatar);

        return user;
    }

    @Override
    public String getNicknameById(Long id) {
        return bloggerProfileMapper.selectByPrimaryKey(id).getNickname();
    }
}
