# java使用ElasticSearch

## 1.引用

 maven

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.0.0</version>
</dependency>
```

Gradle

```properties
dependencies {
    compile 'org.elasticsearch.client:elasticsearch-rest-high-level-client:7.0.0'
}
```

## 2.初始化

```java
RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")));
```

高级客户端用完之后可以用`client.close();`关闭

## Apache Lucene默认的评分公式

TF/IDF(词频/逆文档频率)算法

计算文档评分需要考虑的因子 :

- 文档权重(document boost) : 索引期赋予某个文档的权重值.

- 字段权重(field boost) : 查询期赋予某个字段的权重值.

- 协调因子(coord) : 基于文档中词项命中个数的协调因子,一个文档中命中了查询中的词项越多,得分越高

- 逆文档频率(inverse document frequent) : 一个基于词项的因子,用来告诉评分公式该词项有多么罕见.逆文档频率越低,词项越罕见.评分公式利用该因子为包含罕见词项的文档加权

- 长度范数（length norm）：每个字段的基于词项个数的归一化因子（在索引期计算出来并存储在索引中）。一个字段包含的词项数越多，该因子的权重越低，这意味着Apache Lucene评分公式更“喜欢”包含更少词项的字段。

- 词频（term frequency）：一个基于词项的因子，用来表示一个词项在某个文档中出现了多少次。词频越高，文档得分越高。

- 查询范数（query norm）：一个基于查询的归一化因子，它等于查询中词项的权重平方和。查询范数使不同查询的得分能相互比较，尽管这种比较通常是困难且不可行的。

评分有关的因素 :

- 越多罕见的词项被匹配上,文档的得分越高
- 文档字段越短(包含更少的词项),文档得分越高
- 权重越高(不论是索引期还是查询期赋予的权重值),文档得分越高

ElasticSearch使用了Lucene的评分功能,但不限于Lucene的评分功能.
用户可以使用各种不同的查询类型以精确控制文档评分的计算(如custom_boost_factor查询,constant_score查询,custom_score查询),还可以使用脚本(scripting)来改变文档得分,还可以使用ElasticSearch0.90中出现的二次评分功能,通过在返回文档集之上执行另一个查询,重新计算前N个文档的文档得分.


