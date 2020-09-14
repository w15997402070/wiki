# 文档生成

## 先安装nodeJs和npm

### 在npm环境下执行  

[apiDoc官网](http://apidocjs.com)

    npm install apidoc -g

### 在java方法上添加

```java
/**
     * @api {GET} api/information/updateInfoViewCount 更新资讯浏览量
     * @apiGroup information
     * @apiVersion 0.0.1
     * @apiDescription 根据资讯UUID更新资讯浏览量
     * @apiParam {String} 	uuid			[uuid(必填)]
     * @apiParamExample {Json} 请求样例
     * {uuid:xxx}
     * @apiSuccess {String} msg [返回信息]
     * @apiSuccess {String} code [200成功;500失败]
     * @apiSuccessExample {json} 返回样例:
     *                     {code: "200",msg:"更新成功"}
     */
```

### 在项目中添加 apidoc.json

```json
{
  "name": "接口文档",
  "version": "1.0.0",
  "description": "资讯项目API文档",
  "title": "info API",
  "url": "http://localhost:8052/",
  "forceLanguage": "zh-cn"
}
```

### 在项目文件夹下执行

    apidoc  -i ./src/main/java/com/zjcpo/info/web -o ./apidoc/

### 帮助 : 

查看帮助

    apidoc --help

```shell
Options:
  -f --file-filters <file-filters>         RegEx-Filter to select files that should be parsed (multiple -f can be used). (default: [])
  -e, --exclude-filters <exclude-filters>  RegEx-Filter to select files / dirs that should not be parsed (many -e can be used). (default: [])
  -i, --input <input>                      输入的文件夹Input/source dirname. (default: [])
  -o, --output <output>                    输出文件夹Output dirname. (default: "./doc/")
  -t, --template <template>                Use template for output files. (default: "C:\\Users\\wang\\AppData\\Roaming\\npm\\node_modules\\apidoc\\template\\")
  -c, --config <config>                    Path to directory containing config file (apidoc.json). (default: "./")
  -p, --private                            Include private APIs in output.
  -v, --verbose                            Verbose debug output.
  --debug                                  Show debug messages.
  --color                                  Turn off log color.
  --parse                                  Parse only the files and return the data, no file creation.
  --parse-filters <parse-filters>          Optional user defined filters. Format name=filename (default: [])
  --parse-languages <parse-languages>      Optional user defined languages. Format name=filename (default: [])
  --parse-parsers <parse-parsers>          Optional user defined parsers. Format name=filename (default: [])
  --parse-workers <parse-workers>          Optional user defined workers. Format name=filename (default: [])
  --silent                                 Turn all output off.
  --simulate                               Execute but not write any file.
  --markdown [markdown]                    Turn off default markdown parser or set a file to a custom parser. (default: true)
  --line-ending <line-ending>              Turn off autodetect line-ending. Allowed values: LF, CR, CRLF.
  --encoding <encoding>                    Set the encoding of the source code. [utf8]. (default: "utf8")
  -h, --help                               output usage information

```