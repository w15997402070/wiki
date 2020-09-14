# WordCount

[toc]



1. 创建maven项目

2. 在pom.xml文件中加入依赖包

   ```xml
   <dependency>
       <groupId>org.apache.storm</groupId>
       <artifactId>storm-core</artifactId>
       <version>2.1.0</version>
   </dependency>
   ```

3. 编写代码

```java
package org.example.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.ShellBolt;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

public class StormDemo {

    public static void main(String[] args) throws Exception {
        new StormDemo().runLocal(args);
    }

    protected int runLocal(String[] strings) throws Exception {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("spout",new WordSpout(),1);

        topologyBuilder.setBolt("split",new SplitSentenceBolt(),1).shuffleGrouping("spout");
        topologyBuilder.setBolt("count",new WordCount(),1).fieldsGrouping("split",new Fields("word"));

        Config config = new Config();
//        config.setDebug(true);
        String topologyName = "word-count";
        config.setNumWorkers(1);
        //LocalCluster表示本地执行,也就是可以直接在idea中run运行
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology(topologyName,config,topologyBuilder.createTopology());
        try {
            Thread.sleep( 5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        localCluster.killTopology("word_count");
        localCluster.shutdown();
        return 1;
    }



    public static class SplitSentenceBolt extends BaseRichBolt{
        private OutputCollector collector;
        private int index = 0;

        @Override
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String sentence = tuple.getString(0);
            System.out.println("语句 : "+sentence);
            String[] s = sentence.split(" ");
            for (int i = 0; i < s.length ; i++) {
                System.out.println("单词 : "+s[i]);
                collector.emit(new Values(s[i]));
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }
    public static class SplitSentence extends ShellBolt implements IRichBolt{

        public SplitSentence() {
            super("python", "splitsentence.py");
        }
        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            return null;
        }
    }

    public static class WordCount extends BaseBasicBolt {
        Map<String, Integer> counts = new HashMap<String, Integer>();

        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {

            String word = tuple.getString(0);
            Integer count = counts.get(word);
            if (count == null){
                count = 0;
            }
            count++;
            counts.put(word,count);
            collector.emit(new Values(word,count));

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
        }

        @Override
        public void cleanup() {
            counts.forEach((k,v) -> {
                System.out.println("结果 : "+k + "---"+v);
            });
        }
    }
}

```



```java
package org.example.storm;


import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

public class WordSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(WordSpout.class);

    SpoutOutputCollector collector;
    Random random;

    /**
     * 初始化方法
     * @param map
     * @param topologyContext
     * @param spoutOutputCollector
     */
    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        this.random = new Random();
    }

    /**
     * storm 不停的调这个方法
     *    类似 while(true){
     *        nextTuple()
     *    }
     */
    @Override
    public void nextTuple() {
        Utils.sleep(1000);
        String[] sentences = new String[]{
                sentence("the cow jumped over the moon"), sentence("an apple a day keeps the doctor away"),
                sentence("four score and seven years ago"), sentence("snow white and the seven dwarfs"), sentence("i am at two with nature")
        };
        final String sentence = sentences[random.nextInt(sentences.length)];
        LOG.debug("Emitting tuple: {}", sentence);
        collector.emit(new Values(sentence));

    }
    protected String sentence(String input) {
        return input;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}

```

4. 编程模型

   ![image-20200418212258991](D:\data\notes\notes\大数据\storm\IIDEA+Storm+WordCount\image-20200418212258991.png)

   Spout是数据源`WordSpout`中`WordSpout # nextTuple() `不断的调用产生数据,发送到Bolt中处理数据

5. 