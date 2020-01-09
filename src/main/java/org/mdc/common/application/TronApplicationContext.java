package org.mdc.common.application;

import org.mdc.common.overlay.discover.DiscoverServer;
import org.mdc.common.overlay.discover.node.NodeManager;
import org.mdc.common.overlay.server.ChannelManager;
import org.mdc.core.db.Manager;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TronApplicationContext extends AnnotationConfigApplicationContext {

  public TronApplicationContext() {
  }

  public TronApplicationContext(DefaultListableBeanFactory beanFactory) {
    super(beanFactory);
  }

  public TronApplicationContext(Class<?>... annotatedClasses) {
    super(annotatedClasses);
  }

  public TronApplicationContext(String... basePackages) {
    super(basePackages);
  }

  @Override
  public void destroy() {

    Application appT = ApplicationFactory.create(this);
    appT.shutdownServices();
    appT.shutdown();

    DiscoverServer discoverServer = getBean(DiscoverServer.class);
    discoverServer.close();
    ChannelManager channelManager = getBean(ChannelManager.class);
    channelManager.close();
    NodeManager nodeManager = getBean(NodeManager.class);
    nodeManager.close();

    Manager dbManager = getBean(Manager.class);
    dbManager.stopRepushThread();
    dbManager.stopRepushTriggerThread();
    super.destroy();
  }
}
