1. 下载kibana  https://www.elastic.co/downloads/kibana

2. 安装x-pack
   页面地址 : https://www.elastic.co/guide/en/kibana/5.6/installing-xpack-kb.html
   下载地址 : https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.6.1.zip

3. 设置ElasticSearch 配置文件elasticsearch.yml 加上
   ### 设置为不要密码登陆
   xpack.security.enabled: false

   ### 设置要密码登陆
    xpack.security.enabled: false
   默认用户名 : elastic   默认密码 : changeme
   这时elasticSearch 127.0.0.1:9200 登不进去  解决方案 : ....(暂未找到)
