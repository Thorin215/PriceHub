package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/versions")
@CrossOrigin(origins = "*")
public class VersionController {

    @Autowired
    private VersionService versionService;

    // 创建版本
    @PostMapping("/create")
    public ResponseEntity<Response> createVersion(@RequestBody Version version) {
        Version createdVersion = versionService.createVersion(version.getGoodId(), version.getPrice());
        Response response = new Response("success", HttpStatus.OK.value(), createdVersion);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 根据商品ID获取版本
    @GetMapping("/query/{goodId}")
    public ResponseEntity<ListResponse> getVersionsByGoodId(@PathVariable Long goodId) {
        List<Version> versions = versionService.getVersionsByGoodId(goodId);
        ListResponse response = new ListResponse("success", HttpStatus.OK.value(), versions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 其他与版本相关的接口

    static class Response {
        private String msg;
        private int code;
        private Object data; // 改为 Object 类型

        public Response(String msg, int code, Object data) {
            this.msg = msg;
            this.code = code;
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    static class ListResponse {
        private String msg;
        private int code;
        private List<Version> data;

        public ListResponse(String msg, int code, List<Version> data) {
            this.msg = msg;
            this.code = code;
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<Version> getData() {
            return data;
        }

        public void setData(List<Version> data) {
            this.data = data;
        }
    }
}
