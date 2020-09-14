# Flume安装

[toc]

1.下载安装包

`apache-flume-1.9.0-bin.tar.gz`

2. 解压缩

   ` tar -zxvf apache-flume-1.9.0-bin.tar.gz`

3. 配置文件

   `cp flume-conf.properties.template flume.conf`

   `cp flume-env.sh.template flume-env.sh`

   在`flume.conf`配置文件中配置

   ```conf
   # Define a memory channel called ch1 on agent1
   agent1.channels.ch1.type = memory
    
   # Define an Avro source called avro-source1 on agent1 and tell it
   # to bind to 0.0.0.0:41414. Connect it to channel ch1.
   agent1.sources.avro-source1.channels = ch1
   agent1.sources.avro-source1.type = avro
   agent1.sources.avro-source1.bind = 0.0.0.0
   agent1.sources.avro-source1.port = 41414
    
   # Define a logger sink that simply logs all events it receives
   # and connect it to the other end of the same channel.
   agent1.sinks.log-sink1.channel = ch1
   agent1.sinks.log-sink1.type = logger
    
   # Finally, now that we've defined all of our components, tell
   # agent1 which ones we want to activate.
   agent1.channels = ch1
   agent1.sources = avro-source1
   agent1.sinks = log-sink1
   ```

   

4. 启动

   `bin/flume-ng agent --conf ./conf/ -f conf/flume.conf -Dflume.root.logger=DEBUG,console -n agent1`

   将会看到

   ```bash
   2020-04-14 20:23:57,314 (lifecycleSupervisor-1-0) [INFO - org.apache.flume.node.PollingPropertiesFileConfigurationProvider.start(PollingPropertiesFileConfigurationProvider.java:62)] Configuration provider starting
   2020-04-14 20:23:57,330 (lifecycleSupervisor-1-0) [DEBUG - org.apache.flume.node.PollingPropertiesFileConfigurationProvider.start(PollingPropertiesFileConfigurationProvider.java:79)] Configuration provider started
   2020-04-14 20:23:57,334 (conf-file-poller-0) [DEBUG - org.apache.flume.node.PollingPropertiesFileConfigurationProvider$FileWatcherRunnable.run(PollingPropertiesFileConfigurationProvider.java:131)] Checking file:conf/flume.conf for changes
   2020-04-14 20:23:57,335 (conf-file-poller-0) [INFO - org.apache.flume.node.PollingPropertiesFileConfigurationProvider$FileWatcherRunnable.run(PollingPropertiesFileConfigurationProvider.java:138)] Reloading configuration file:conf/flume.conf
   2020-04-14 20:23:57,340 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1203)] Processing:avro-source1
   2020-04-14 20:23:57,340 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1207)] Created context for avro-source1: port
   2020-04-14 20:23:57,358 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1203)] Processing:avro-source1
   2020-04-14 20:23:57,358 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1203)] Processing:avro-source1
   2020-04-14 20:23:57,358 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1203)] Processing:log-sink1
   2020-04-14 20:23:57,358 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1207)] Created context for log-sink1: channel
   2020-04-14 20:23:57,359 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1203)] Processing:ch1
   2020-04-14 20:23:57,359 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1207)] Created context for ch1: type
   2020-04-14 20:23:57,359 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1203)] Processing:avro-source1
   2020-04-14 20:23:57,359 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addProperty(FlumeConfiguration.java:1117)] Added sinks: log-sink1 Agent: agent1
   2020-04-14 20:23:57,359 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.addComponentConfig(FlumeConfiguration.java:1203)] Processing:log-sink1
   2020-04-14 20:23:57,359 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.isValid(FlumeConfiguration.java:350)] Starting validation of configuration for agent: agent1
   2020-04-14 20:23:57,359 (conf-file-poller-0) [INFO - org.apache.flume.conf.LogPrivacyUtil.<clinit>(LogPrivacyUtil.java:51)] Logging of configuration details is disabled. To see configuration details in the log run the agent with -Dorg.apache.flume.log.printconfig=true JVM argument. Please note that this is not recommended in production systems as it may leak private information to the logfile.
   2020-04-14 20:23:57,359 (conf-file-poller-0) [WARN - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.validateConfigFilterSet(FlumeConfiguration.java:623)] Agent configuration for 'agent1' has no configfilters.
   2020-04-14 20:23:57,406 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.validateChannels(FlumeConfiguration.java:583)] Created channel ch1
   2020-04-14 20:23:57,410 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration$AgentConfiguration.validateSinks(FlumeConfiguration.java:861)] Creating sink: log-sink1 using LOGGER
   2020-04-14 20:23:57,411 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration.validateConfiguration(FlumeConfiguration.java:158)] Channels:ch1
   
   2020-04-14 20:23:57,412 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration.validateConfiguration(FlumeConfiguration.java:159)] Sinks log-sink1
   
   2020-04-14 20:23:57,412 (conf-file-poller-0) [DEBUG - org.apache.flume.conf.FlumeConfiguration.validateConfiguration(FlumeConfiguration.java:160)] Sources avro-source1
   
   2020-04-14 20:23:57,412 (conf-file-poller-0) [INFO - org.apache.flume.conf.FlumeConfiguration.validateConfiguration(FlumeConfiguration.java:163)] Post-validation flume configuration contains configuration for agents: [agent1]
   2020-04-14 20:23:57,412 (conf-file-poller-0) [INFO - org.apache.flume.node.AbstractConfigurationProvider.loadChannels(AbstractConfigurationProvider.java:151)] Creating channels
   2020-04-14 20:23:57,433 (conf-file-poller-0) [INFO - org.apache.flume.channel.DefaultChannelFactory.create(DefaultChannelFactory.java:42)] Creating instance of channel ch1 type memory
   2020-04-14 20:23:57,460 (conf-file-poller-0) [INFO - org.apache.flume.node.AbstractConfigurationProvider.loadChannels(AbstractConfigurationProvider.java:205)] Created channel ch1
   2020-04-14 20:23:57,461 (conf-file-poller-0) [INFO - org.apache.flume.source.DefaultSourceFactory.create(DefaultSourceFactory.java:41)] Creating instance of source avro-source1, type avro
   2020-04-14 20:23:57,513 (conf-file-poller-0) [INFO - org.apache.flume.sink.DefaultSinkFactory.create(DefaultSinkFactory.java:42)] Creating instance of sink: log-sink1, type: logger
   2020-04-14 20:23:57,517 (conf-file-poller-0) [INFO - org.apache.flume.node.AbstractConfigurationProvider.getConfiguration(AbstractConfigurationProvider.java:120)] Channel ch1 connected to [avro-source1, log-sink1]
   2020-04-14 20:23:57,538 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:162)] Starting new configuration:{ sourceRunners:{avro-source1=EventDrivenSourceRunner: { source:Avro source avro-source1: { bindAddress: 0.0.0.0, port: 41414 } }} sinkRunners:{log-sink1=SinkRunner: { policy:org.apache.flume.sink.DefaultSinkProcessor@275e7b61 counterGroup:{ name:null counters:{} } }} channels:{ch1=org.apache.flume.channel.MemoryChannel{name: ch1}} }
   2020-04-14 20:23:57,538 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:169)] Starting Channel ch1
   2020-04-14 20:23:57,550 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:184)] Waiting for channel: ch1 to start. Sleeping for 500 ms
   2020-04-14 20:23:57,668 (lifecycleSupervisor-1-0) [INFO - org.apache.flume.instrumentation.MonitoredCounterGroup.register(MonitoredCounterGroup.java:119)] Monitored counter group for type: CHANNEL, name: ch1: Successfully registered new MBean.
   2020-04-14 20:23:57,668 (lifecycleSupervisor-1-0) [INFO - org.apache.flume.instrumentation.MonitoredCounterGroup.start(MonitoredCounterGroup.java:95)] Component type: CHANNEL, name: ch1 started
   2020-04-14 20:23:58,051 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:196)] Starting Sink log-sink1
   2020-04-14 20:23:58,052 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:207)] Starting Source avro-source1
   2020-04-14 20:23:58,053 (lifecycleSupervisor-1-4) [INFO - org.apache.flume.source.AvroSource.start(AvroSource.java:193)] Starting Avro source avro-source1: { bindAddress: 0.0.0.0, port: 41414 }...
   2020-04-14 20:23:58,085 (SinkRunner-PollingRunner-DefaultSinkProcessor) [DEBUG - org.apache.flume.SinkRunner$PollingRunner.run(SinkRunner.java:141)] Polling sink runner starting
   2020-04-14 20:23:58,631 (lifecycleSupervisor-1-4) [INFO - org.apache.flume.instrumentation.MonitoredCounterGroup.register(MonitoredCounterGroup.java:119)] Monitored counter group for type: SOURCE, name: avro-source1: Successfully registered new MBean.
   2020-04-14 20:23:58,631 (lifecycleSupervisor-1-4) [INFO - org.apache.flume.instrumentation.MonitoredCounterGroup.start(MonitoredCounterGroup.java:95)] Component type: SOURCE, name: avro-source1 started
   2020-04-14 20:23:58,645 (lifecycleSupervisor-1-4) [INFO - org.apache.flume.source.AvroSource.start(AvroSource.java:219)] Avro source avro-source1 started.
   2020-04-14 20:24:28,072 (conf-file-poller-0) [DEBUG - org.apache.flume.node.PollingPropertiesFileConfigurationProvider$FileWatcherRunnable.run(PollingPropertiesFileConfigurationProvider.java:131)] Checking file:conf/flume.conf for changes
   ```

5. 启动`avro-client`客户端

   另开一个窗口执行

   `bin/flume-ng avro-client --conf conf -H localhost -p ``41414` `-F /etc/passwd -Dflume.root.logger=DEBUG,console`

   可以看到

   ```bash
   2020-04-14 20:26:24,475 (main) [DEBUG - org.apache.flume.api.AbstractRpcClient.parseBatchSize(AbstractRpcClient.java:75)] Batch size string = 5
   2020-04-14 20:26:24,516 (main) [INFO - org.apache.flume.api.NettyAvroRpcClient.configure(NettyAvroRpcClient.java:594)] Using default maxIOWorkers
   2020-04-14 20:26:25,322 (main) [DEBUG - org.apache.flume.client.avro.AvroCLIClient.run(AvroCLIClient.java:239)] Finished
   2020-04-14 20:26:25,322 (main) [DEBUG - org.apache.flume.client.avro.AvroCLIClient.run(AvroCLIClient.java:242)] Closing reader
   2020-04-14 20:26:25,322 (main) [DEBUG - org.apache.flume.client.avro.AvroCLIClient.run(AvroCLIClient.java:246)] Closing RPC client
   2020-04-14 20:26:25,331 (main) [DEBUG - org.apache.flume.client.avro.AvroCLIClient.main(AvroCLIClient.java:88)] Exiting
   ```

   在服务端窗口中可以看到

   ```bash
   2020-04-14 20:26:24,819 (New I/O server boss #3) [INFO - org.apache.avro.ipc.NettyServer$NettyServerAvroHandler.handleUpstream(NettyServer.java:171)] [id: 0x1dcd207e, /127.0.0.1:52466 => /127.0.0.1:41414] OPEN
   2020-04-14 20:26:24,819 (New I/O worker #1) [INFO - org.apache.avro.ipc.NettyServer$NettyServerAvroHandler.handleUpstream(NettyServer.java:171)] [id: 0x1dcd207e, /127.0.0.1:52466 => /127.0.0.1:41414] BOUND: /127.0.0.1:41414
   2020-04-14 20:26:24,820 (New I/O worker #1) [INFO - org.apache.avro.ipc.NettyServer$NettyServerAvroHandler.handleUpstream(NettyServer.java:171)] [id: 0x1dcd207e, /127.0.0.1:52466 => /127.0.0.1:41414] CONNECTED: /127.0.0.1:52466
   2020-04-14 20:26:25,257 (New I/O worker #1) [DEBUG - org.apache.flume.source.AvroSource.appendBatch(AvroSource.java:336)] Avro source avro-source1: Received avro event batch of 5 events.
   2020-04-14 20:26:25,307 (New I/O worker #1) [DEBUG - org.apache.flume.source.AvroSource.appendBatch(AvroSource.java:336)] Avro source avro-source1: Received avro event batch of 5 events.
   2020-04-14 20:26:25,311 (New I/O worker #1) [DEBUG - org.apache.flume.source.AvroSource.appendBatch(AvroSource.java:336)] Avro source avro-source1: Received avro event batch of 5 events.
   2020-04-14 20:26:25,317 (New I/O worker #1) [DEBUG - org.apache.flume.source.AvroSource.appendBatch(AvroSource.java:336)] Avro source avro-source1: Received avro event batch of 5 events.
   2020-04-14 20:26:25,320 (New I/O worker #1) [DEBUG - org.apache.flume.source.AvroSource.appendBatch(AvroSource.java:336)] Avro source avro-source1: Received avro event batch of 2 events.
   2020-04-14 20:26:25,327 (New I/O worker #1) [INFO - org.apache.avro.ipc.NettyServer$NettyServerAvroHandler.handleUpstream(NettyServer.java:171)] [id: 0x1dcd207e, /127.0.0.1:52466 :> /127.0.0.1:41414] DISCONNECTED
   2020-04-14 20:26:25,327 (New I/O worker #1) [INFO - org.apache.avro.ipc.NettyServer$NettyServerAvroHandler.handleUpstream(NettyServer.java:171)] [id: 0x1dcd207e, /127.0.0.1:52466 :> /127.0.0.1:41414] UNBOUND
   2020-04-14 20:26:25,327 (New I/O worker #1) [INFO - org.apache.avro.ipc.NettyServer$NettyServerAvroHandler.handleUpstream(NettyServer.java:171)] [id: 0x1dcd207e, /127.0.0.1:52466 :> /127.0.0.1:41414] CLOSED
   2020-04-14 20:26:25,327 (New I/O worker #1) [INFO - org.apache.avro.ipc.NettyServer$NettyServerAvroHandler.channelClosed(NettyServer.java:209)] Connection to /127.0.0.1:52466 disconnected.
   2020-04-14 20:26:28,074 (conf-file-poller-0) [DEBUG - org.apache.flume.node.PollingPropertiesFileConfigurationProvider$FileWatcherRunnable.run(PollingPropertiesFileConfigurationProvider.java:131)] Checking file:conf/flume.conf for changes
   2020-04-14 20:26:28,101 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 72 6F 6F 74 3A 78 3A 30 3A 30 3A 72 6F 6F 74 3A root:x:0:0:root: }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 62 69 6E 3A 78 3A 31 3A 31 3A 62 69 6E 3A 2F 62 bin:x:1:1:bin:/b }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 64 61 65 6D 6F 6E 3A 78 3A 32 3A 32 3A 64 61 65 daemon:x:2:2:dae }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 61 64 6D 3A 78 3A 33 3A 34 3A 61 64 6D 3A 2F 76 adm:x:3:4:adm:/v }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 6C 70 3A 78 3A 34 3A 37 3A 6C 70 3A 2F 76 61 72 lp:x:4:7:lp:/var }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 73 79 6E 63 3A 78 3A 35 3A 30 3A 73 79 6E 63 3A sync:x:5:0:sync: }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 73 68 75 74 64 6F 77 6E 3A 78 3A 36 3A 30 3A 73 shutdown:x:6:0:s }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 68 61 6C 74 3A 78 3A 37 3A 30 3A 68 61 6C 74 3A halt:x:7:0:halt: }
   2020-04-14 20:26:28,102 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 6D 61 69 6C 3A 78 3A 38 3A 31 32 3A 6D 61 69 6C mail:x:8:12:mail }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 6F 70 65 72 61 74 6F 72 3A 78 3A 31 31 3A 30 3A operator:x:11:0: }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 67 61 6D 65 73 3A 78 3A 31 32 3A 31 30 30 3A 67 games:x:12:100:g }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 66 74 70 3A 78 3A 31 34 3A 35 30 3A 46 54 50 20 ftp:x:14:50:FTP  }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 6E 6F 62 6F 64 79 3A 78 3A 39 39 3A 39 39 3A 4E nobody:x:99:99:N }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 73 79 73 74 65 6D 64 2D 6E 65 74 77 6F 72 6B 3A systemd-network: }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 64 62 75 73 3A 78 3A 38 31 3A 38 31 3A 53 79 73 dbus:x:81:81:Sys }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 70 6F 6C 6B 69 74 64 3A 78 3A 39 39 39 3A 39 39 polkitd:x:999:99 }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 70 6F 73 74 66 69 78 3A 78 3A 38 39 3A 38 39 3A postfix:x:89:89: }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 63 68 72 6F 6E 79 3A 78 3A 39 39 38 3A 39 39 36 chrony:x:998:996 }
   2020-04-14 20:26:28,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 73 73 68 64 3A 78 3A 37 34 3A 37 34 3A 50 72 69 sshd:x:74:74:Pri }
   2020-04-14 20:26:28,104 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 64 6F 63 6B 65 72 72 6F 6F 74 3A 78 3A 39 39 37 dockerroot:x:997 }
   2020-04-14 20:26:28,104 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 76 62 6F 78 61 64 64 3A 78 3A 39 39 36 3A 31 3A vboxadd:x:996:1: }
   2020-04-14 20:26:28,104 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 6D 79 73 71 6C 3A 78 3A 32 37 3A 32 37 3A 4D 79 mysql:x:27:27:My }
   2020-04-14 20:26:58,075 (conf-file-poller-0) [DEBUG - org.apache.flume.node.PollingPropertiesFileConfigurationProvider$FileWatcherRunnable.run(PollingPropertiesFileConfigurationProvider.java:131)] Checking file:conf/flume.conf for changes
   ```

   

6. 

## netcat配置

```conf
# Name the components on this agent
#给那三个组件取个名字
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
#类型, 从网络端口接收数据,在本机启动, 所以localhost, type=spoolDir采集目录源,目录里有就采
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

# Describe the sink
a1.sinks.k1.type = logger

# Use a channel which buffers events in memory
#下沉的时候是一批一批的, 下沉的时候是一个个eventChannel参数解释：
#capacity：默认该通道中最大的可以存储的event数量
#trasactionCapacity：每次最大可以从source中拿到或者送到sink中的event数量
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```

启动`flume-ng`服务端

```bash
$ bin/flume-ng agent --conf conf --conf-file conf/netcat-logger.conf --name a1 -Dflume.root.logger=INFO,console
```



另开一个窗口

输入`telnet localhost 44444`

如果显示没有安装`telnet`就可以使用命令`yum install telnet`安装

连接之后就可以通信

```bash
[root@master flume]# telnet localhost 44444
Trying ::1...
telnet: connect to address ::1: Connection refused
Trying 127.0.0.1...
Connected to localhost.
Escape character is '^]'.
123
OK
mylog
OK
```

上面`123`,`mylog`是自己输入的

服务端就会显示

```bash
2020-04-14 20:47:04,809 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 31 32 33 0D                                     123. }
2020-04-14 20:47:13,936 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 6D 79 6C 6F 67 0D                               mylog. }
```

