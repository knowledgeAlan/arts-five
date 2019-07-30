# Mybatis源码分析

- mybatis3.5.2版本



| 名称              | 意义                                                         |
| ----------------- | ------------------------------------------------------------ |
| Configuration     | 管理 mysql-config.xml 全局配置关系类                         |
| SqlSessionFactory | Session 管理工厂接口                                         |
| Session           | SqlSession 是一个面向用户(程序员)的接口。SqlSession 中提供了很多操作数据库的方法 |
| Executor          | 执行器是一个接口(基本执行器、缓存执行器)<br>作用:SqlSession 内部通过执行器操作数据库 |
| MappedStatement   | 底层封装对象<br/>作用:对操作数据库存储封装，包括 sql 语句、输入输出参数 |
| StatementHandler  | 具体操作数据库相关的 handler 接口                            |
| ResultSetHandler  | 具体操作数据库返回结果的 handler 接口                        |



## mybatis工作流程图



##![画图](/Users/zhongzuoming/Desktop/java/整理质料/web质料/java/md/images/mybatis/mybatis流程1.png)SqlSessionFactory的类图 



![](/Users/zhongzuoming/Desktop/java/整理质料/web质料/java/md/images/mybatis/image-20190725222526066.png) 



## TransactionFactory事物管理类图

![image-20190726144424452](/Users/zhongzuoming/Desktop/java/整理质料/web质料/java/md/images/mybatis/TransactionFactory.png)

org.apache.ibatis.executor.BaseExecutor属性有Transaction，org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession()->openSessionFromDataSource初始化executor，配置Transaction



```java
public abstract class BaseExecutor implements Executor {

  private static final Log log = LogFactory.getLog(BaseExecutor.class);

  protected Transaction transaction;
  …………
```

org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSessionFromDataSource

```java
 
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
      final Environment environment = configuration.getEnvironment();
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
      final Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
```

## 执行器executor类图

![image-20190726145425870](/Users/zhongzuoming/Desktop/java/整理质料/web质料/java/md/images/mybatis/executor.png)executor默认使用CachingExecutor，因为缓存默认开启

```java
  public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    executorType = executorType == null ? defaultExecutorType : executorType;
    executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
    Executor executor;
    if (ExecutorType.BATCH == executorType) {
      executor = new BatchExecutor(this, transaction);
    } else if (ExecutorType.REUSE == executorType) {
      executor = new ReuseExecutor(this, transaction);
    } else {
      executor = new SimpleExecutor(this, transaction);
    }
    if (cacheEnabled) {
      executor = new CachingExecutor(executor);
    }
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
  }

```



## sqlsession类图

![image-20190726150658249](/Users/zhongzuoming/Desktop/java/整理质料/web质料/java/md/images/mybatis/sqlsession.png)

1：sqlSession默认实现DefaultSqlSession， Blog blog = session.selectOne(                   "com.zzm.dao.BlogMapper.selectBlog", 1)查询最后执行org.apache.ibatis.executor.BaseExecutor#queryFromDatabase()访问数据库并查询

2：mapper接口定义方法，mapper.selectXXX()查询，是通过java动态代理，首先执行org.apache.ibatis.binding.MapperMethod#execute()判断查询查询类型，最后sqlSession.selectOne()

执行数据查询，同上

```java
public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    switch (command.getType()) {
     ……………………
      case SELECT:
        if (method.returnsVoid() && method.hasResultHandler()) {
          executeWithResultHandler(sqlSession, args);
          result = null;
        } else if (method.returnsMany()) {
          result = executeForMany(sqlSession, args);
        } else if (method.returnsMap()) {
          result = executeForMap(sqlSession, args);
        } else if (method.returnsCursor()) {
          result = executeForCursor(sqlSession, args);
        } else {
          Object param = method.convertArgsToSqlCommandParam(args);
          result = sqlSession.selectOne(command.getName(), param);
          if (method.returnsOptional()
              && (result == null || !method.getReturnType().equals(result.getClass()))) {
            result = Optional.ofNullable(result);
          }
        }
        break;
   …………………………
  }
```

