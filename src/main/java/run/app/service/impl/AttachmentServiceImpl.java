package run.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import run.app.entity.DTO.BaseResponse;
import run.app.entity.DTO.DataGrid;
import run.app.entity.DTO.Picture;
import run.app.entity.model.BloggerPicture;
import run.app.entity.model.BloggerPictureExample;
import run.app.exception.BadRequestException;
import run.app.mapper.BloggerPictureMapper;
import run.app.security.token.TokenService;
import run.app.service.AttachmentService;
import run.app.service.UserService;
import run.app.util.UploadUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/9/3 19:38
 * Description: ://TODO ${END}
 */
@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService {


    @Autowired
    BloggerPictureMapper bloggerPictureMapper;



    @Autowired
    UserService userService;


    @Autowired
    TokenService tokenService;

    @Override
    public String selectPicById(Integer id) {
        return bloggerPictureMapper.selectByPrimaryKey(id).getTitle();
    }


    @Override
    public DataGrid getAttachmentList(int pageSize, int pageNum, String token) {

        int id = tokenService.getUserIdWithToken(token);

        BloggerPictureExample bloggerPictureExample = new BloggerPictureExample();

        bloggerPictureExample.setOrderByClause("upload_date desc");

        BloggerPictureExample.Criteria criteria = bloggerPictureExample.createCriteria();

        criteria.andBloggerIdEqualTo(id);

        PageHelper.startPage(pageNum,pageSize);

        List<BloggerPicture> bloggerPictures = bloggerPictureMapper.selectByExample(bloggerPictureExample);

        PageInfo<BloggerPicture> bloggerPicturePageInfo = new PageInfo<>(bloggerPictures);


        List<Picture> pictures = bloggerPictures.stream().filter(Objects::nonNull).map(item ->{
            return new Picture(item.getId(),item.getTitle(),item.getBewrite());
                }
        ).collect(Collectors.toList());


        DataGrid dataGrid = new DataGrid();

        dataGrid.setTotal(bloggerPicturePageInfo.getTotal());
        dataGrid.setRows(pictures);

        return dataGrid;
    }

    @Override
    @Transactional
    public BaseResponse uploadFile(MultipartFile file, String token) {
        UploadUtil instance = UploadUtil.getInstance();
        String filename = instance.uploadFile(file).orElseThrow(()->new BadRequestException("用户上传图片失败"));

        int user_id = tokenService.getUserIdWithToken(token);

        BloggerPicture bloggerPicture = new BloggerPicture();

        bloggerPicture.setTitle(filename);

        bloggerPicture.setBloggerId(user_id);

        bloggerPicture.setUploadDate(new Date());

        bloggerPictureMapper.insertSelective(bloggerPicture);

        BaseResponse baseResponse = new BaseResponse();

        baseResponse.setStatus(HttpStatus.OK.value());

        baseResponse.setData(filename);

        return baseResponse;
    }

    @Override
    public int getIdByTitle(String title) {
        BloggerPictureExample bloggerPictureExample = new BloggerPictureExample();
        BloggerPictureExample.Criteria criteria = bloggerPictureExample.createCriteria();
        criteria.andTitleEqualTo(title);

        List<BloggerPicture> bloggerPictures = bloggerPictureMapper.selectByExample(bloggerPictureExample);
        BloggerPicture bloggerPicture = bloggerPictures.stream().filter(Objects::nonNull).findFirst().orElseThrow(() -> new BadRequestException("图片名称有误,或附件已被删除！"));
        return bloggerPicture.getId();

    }


}