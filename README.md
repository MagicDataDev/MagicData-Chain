
<h1 align="center">
  <br>
  <img width=20% src="https://magic-data.s3-ap-southeast-1.amazonaws.com/files/logo.png?raw=true">
  <br>
  MagicData-Chain
  <br>
</h1>

## 什么是 MDC?

随着技术的进一步发展和用户意识的觉醒，隐私保护正愈发受到重视。作为大数据的收集者和分析者，需要严格遵守相关法律法规，并充分考虑用户权益和社会
影响。MagicData(魔数) Chain 致力于解决隐私和便利的两难，MDC 项目以区 块链公链技术为根基，将分布式边缘计算、智能合约、去重加密存储等技术整合， 加载精心设计的金融数学模型，软硬件结合为用户提供最安全可信的，数据存储、 使用、融通等服务。
在传统网络中，用户数据会直接被中心化机构采集，并用于商业目的。这种采集有时经过了用户授权，然而当前更多是在用户不知道的情况下进行采集并使用。
MDC 团队通过构建一条分布式存储、不可篡改且绝对安全的数据公链网络， 用户通过私钥将上链数据完全掌握在自己手中，其他任何人都无法私自查看，可以 实现用户数据的安全存储、计算、传输。
在 MDC 网络中，任何能够采集到的数据均来自用户授权，并且在区块链上不 可篡改，这大大提高了数据的真实性。一方面，用户提供授权的数据说明用户确实 存在强烈的主观需求，这些数据对于精准营销来说更加真实精准;另一方面，区块 链的特性保证了数据不可篡改，大大降低了数据造假的可能性，使得数据更具有参 考意义。
12另外，用户还可以通过私钥将数据授权给企业，为用户提供个性化服务;与传
统网络不同的是，这部分数据所产生的收益，将返还给用户。MDC 网络将用户数据所产生的收益反馈给用户，不仅仅是对用户数据价值的 承认，也大大增强了与用户之间联系的可持续性。用户将重新找回对自身数据，以 及数据产生的价值的控制权，成为自己数据真正的主人。
MDC 网络让数据的价值回归用户，与用户建立起平等、健康、可持续的关 系，不仅可以使用户从中受益，也有利于 MDC 网络自身的长期发展，以及 MDC 建设新经济体愿景的实现。
我们希望，用户能够自主决定自己的每一次点击，每一张照片，每一个行为。我们希望，用户能自己决定，这一条数据是自己的秘密，还是可以用来让世界
变得更美好。我们希望，隐私不会被侵犯，技术不再被用来作恶，而数据能够为数据的主人
产生真正的价值

# 怎么编译

## 前期准备

* JDK 1.8
* Linux Ubuntu 操作系统.
* 最小配置4核

## 使用git

输入以下命令:
```bash
git clone https://github.com/MagicDataDev/MagicData-Chain.git
git checkout -t origin/master
```


## 编译源码

* 编译

```bash
cd MagicData-Chain
./gradlew build
```


* 编译使用 [IntelliJ IDEA](https://www.jetbrains.com/idea/):

  1. 打开 IntelliJ. Select `File` -> `Open`, 然后选择从github上clone下来的 MagicData-Chain文件夹. 然后点击 `Open`按钮.
  2. 在 `Import Project from Gradle` 对话框上勾选 `Use auto-import`. 在 `Gradle JVM` 选择 JDK 1.8 . 然后点击 `OK`.
  3. gradle开始同步, 这个可能需要消耗几分钟的时间.
  4. 打开 Annotations, `Preferences` -> 搜索 `annotations` -> 勾选 `Enable Annotation Processing`.
  5. 同步完成后, 选择 `Gradle` -> `Tasks` -> `build`, 双击 `build` 选项.
  
# Running

* 运行jar包

```bash
java -jar MdcFullNode.jar -p your private key --witness -c your config.conf
Example:
java -jar MdcFullNode.jar -p 650950B193DDDDB35B6E48912DD28F7AB0E7140C1BFDEFD493348F02295BD812 --witness -c /data/mdc/config.conf

```
<details>
<summary>正确的输出</summary>

```bash

14:57:34.258 INFO  [main] [app](FullNode.java:44) Full node running.
14:57:34.607 INFO  [main] [app](Args.java:1218) Bind address wasn't set, Punching to identify it...
14:57:34.613 INFO  [main] [app](Args.java:1221) UDP local bound to: 172.21.0.137
14:57:34.615 INFO  [main] [app](Args.java:1340)

14:57:34.615 INFO  [main] [app](Args.java:1341) ************************ Net config ************************
14:57:34.615 INFO  [main] [app](Args.java:1342) P2P version: 99988
14:57:34.615 INFO  [main] [app](Args.java:1343) Bind IP: 172.21.0.137
14:57:34.615 INFO  [main] [app](Args.java:1344) MAX ACTIVE WITNESS NUM 6
14:57:34.616 INFO  [main] [app](Args.java:1345) External IP: null
14:57:34.616 INFO  [main] [app](Args.java:1346) Listen port: 18888
14:57:34.616 INFO  [main] [app](Args.java:1347) Discover enable: true
14:57:34.616 INFO  [main] [app](Args.java:1348) Active node size: 0
14:57:34.616 INFO  [main] [app](Args.java:1349) Passive node size: 0
14:57:34.616 INFO  [main] [app](Args.java:1350) FastForward node size: 0
14:57:34.616 INFO  [main] [app](Args.java:1351) Seed node size: 1
14:57:34.616 INFO  [main] [app](Args.java:1352) Max connection: 30
14:57:34.616 INFO  [main] [app](Args.java:1353) Max connection with same IP: 2
14:57:34.616 INFO  [main] [app](Args.java:1354) Solidity threads: 8
14:57:34.616 INFO  [main] [app](Args.java:1355) ************************ Backup config ************************
14:57:34.616 INFO  [main] [app](Args.java:1356) Backup listen port: 10001
14:57:34.616 INFO  [main] [app](Args.java:1357) Backup member size: 0
14:57:34.616 INFO  [main] [app](Args.java:1358) Backup priority: 8
14:57:34.617 INFO  [main] [app](Args.java:1359) ************************ Code version *************************
14:57:34.617 INFO  [main] [app](Args.java:1360) Code version : 3.6.1
14:57:34.617 INFO  [main] [app](Args.java:1361) Version name: Lemon-v1.0
14:57:34.617 INFO  [main] [app](Args.java:1362) Version code: 10942
14:57:34.617 INFO  [main] [app](Args.java:1363) ************************ DB config *************************
14:57:34.617 INFO  [main] [app](Args.java:1364) DB version : 2
14:57:34.617 INFO  [main] [app](Args.java:1365) DB engine : LEVELDB
14:57:34.617 INFO  [main] [app](Args.java:1366) ***************************************************************
14:57:34.617 INFO  [main] [app](Args.java:1367)

14:57:34.617 INFO  [main] [app](FullNode.java:58) not in debug mode, it will check energy time
14:57:34.705 INFO  [main] [o.t.c.a.TronApplicationContext](AbstractApplicationContext.java:573) Refreshing org.mdc.common.application.TronApplicationContext@7b02881e: startup date [Wed Jan 08 14:57:34 CST 2020]; root of context hierarchy
14:57:35.524 INFO  [main] [o.s.b.f.a.AutowiredAnnotationBeanPostProcessor](AutowiredAnnotationBeanPostProcessor.java:153) JSR-330 'javax.inject.Inject' annotation found and supported for autowiring
14:57:35.693 INFO  [main] [app](DefaultConfig.java:65) key-value data source created.
14:57:36.150 INFO  [main] [DB](DynamicPropertiesStore.java:1575) update latest block header timestamp = 0
14:57:36.150 INFO  [main] [DB](DynamicPropertiesStore.java:1583) update latest block header number = 0
14:57:36.155 INFO  [main] [DB](DynamicPropertiesStore.java:1591) update latest block header id = 00
14:57:36.156 INFO  [main] [DB](DynamicPropertiesStore.java:1596) update state flag = 0
14:57:36.189 INFO  [main] [DB](DynamicPropertiesStore.java:1698) update allow protobuf number = 0
14:57:36.325 INFO  [main] [DB](Manager.java:520) create genesis block
14:57:36.326 INFO  [main] [DB](Manager.java:526) save block: BlockCapsule

```
</details>


</details>

* 在IntelliJ IDEA运行
  
<details>
<summary>

打开Intellij的配置面板:

<summary>

在`Program arguments` 选项中填入 `--witness`:

</summary>

</details> 
  
然后运行`FullNode::main()`.

# 链接

* [网站](https://home.magicdata.io)
* [文档](https://magicdata.io/app/whitepaper.pdf)

# 项目

* [MDC](https://github.com/MagicDataDev/MagicData-Chain.git)
