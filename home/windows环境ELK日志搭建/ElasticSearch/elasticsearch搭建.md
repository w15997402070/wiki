1. 下载Elasticsearch
    下载地址 : https://www.elastic.co/downloads/elasticsearch

2. 安装X-pack插件
   页面地址 : https://www.elastic.co/guide/en/elasticsearch/reference/5.6/installing-xpack-es.html
   下载地址 :  https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.6.1.zip
3. 在ElasticSearch 配置文件elasticsearch.yml 加上
  ```
    xpack.ml.enabled: false
  ```
