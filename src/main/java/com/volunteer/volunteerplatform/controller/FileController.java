package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.volunteer.volunteerplatform.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    // 读取配置文件里的路径 (D:/volunteer_data/files/)
    @Value("${files.upload.path}")
    private String uploadPath;

    // 读取当前项目的端口号 (比如 9090)
    @Value("${server.port}")
    private String port;

    private static final String IP = "http://localhost";

    /**
     * 文件上传接口
     * @param file 前端传过来的文件
     * @return 图片的访问 URL
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        // 1. 获取文件的原始名称 (比如: test.jpg)
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀 (比如: jpg)
        String type = FileUtil.extName(originalFilename);

        // 2. 生成一个唯一的文件名 (比如: a1b2c3d4.jpg)，防止文件名冲突
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        // 3. 创建保存文件的目录 (如果不存在的话)
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 4. 将文件保存到 D盘指定目录
        File saveFile = new File(uploadPath + fileUUID);
        file.transferTo(saveFile);

        // 5. 生成可以在浏览器访问的 URL (比如: http://localhost:9090/files/a1b2c3d4.jpg)
        // 注意：这里的 /files/ 路径需要下一步在 WebConfig 里配置映射
        String url = IP + ":" + port + "/files/" + fileUUID;

        // 6. 返回 URL 给前端
        return Result.success(url);
    }
}