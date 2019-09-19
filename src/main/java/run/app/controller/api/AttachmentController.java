package run.app.controller.api;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.app.entity.DTO.BaseResponse;
import run.app.entity.DTO.DataGrid;
import run.app.service.AttachmentService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/9/3 19:28
 * Description: :附件控制层
 */
@RequestMapping("api/admin/attachment")
@RestController
@Slf4j
public class AttachmentController {

    @Autowired
    AttachmentService attachmentService;

    @GetMapping("list")
    @ApiOperation("获取所有附件")
    public DataGrid getAttachmentList(@RequestParam("pageSize")int pageSize, @RequestParam("pageNum")int pageNum,
                                      HttpServletRequest request){
        return attachmentService.getAttachmentList(pageSize,pageNum,request.getHeader("Authentication"));
    }


    @PostMapping("upload")
    @ApiOperation("上传图片")
    public BaseResponse uploadFile(MultipartFile file,HttpServletRequest request){
        return attachmentService.uploadFile(file,request.getHeader("Authentication"));
    }

}