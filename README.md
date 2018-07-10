# spring-boot-plus
spring-boot-plus框架是spring boot框架的基础上进行高度扩展的一个框架， 高度集成了Spring Session， Spring Data, 
Spring Cache, Spring WebSocket，Spring Mockito, Spring validation, Redssion，MQ, Redis等前沿框架和服务，
让用户基本需要任何配置构建RESTFul风格的应用，另外框架的DAO层同时实现了Spring Data JPA, Hibernate, Mabatis的各自优势，
基本无需手写SQL语句，也支持用户高度定制SQL，在控制器层， 加入动态视图， 参数校验等高级特性。
## 采用技术框架
新的项目架构以Spring Boot为核心技术框架, 主要集成了以下框架
- Spring Data JPA
- Spring Session
- Spring MVC
- Spring Cache
- Spring WebSocket
- Spring Validation
- Spring Mockito
- Redisson

------------


## Spring Boot 优势
- 遵循“习惯优于配置”的原则，使用Spring Boot只需要很少的配置，大部分的时候我们直接使用默认的配置即可；
- 项目快速搭建，可以无需配置的自动整合第三方的框架；
- 可以完全不使用XML配置文件，只需要自动配置和Java Config；
- 内嵌Servlet容器，降低了对环境的要求，可以使用命令直接执行项目，应用可用jar包执行：java -jar；
- 提供了starter POM, 能够非常方便的进行包管理, 很大程度上减少了jar hell或者dependency hell；
- 运行中应用状态的监控；
- 对主流开发框架的无配置集成；
- 与Docker容器技术无缝整合
- 与云计算的天然继承；

------------


## 项目架构Controller设计
### 统一返回结果
整个项目完全采用动静分离的结构, 后台只负责提供接口服务, 提供给客户端Json格式化的数据, 后端不负责视图的渲染, 页面路由.
Controller类中方法, 全部采用如下结构:
*public Result methodName(Form form);*

### 分页查询
所有分页查询Form表单全部继承PageForm
对于查询条件参数可以加上@Condition注解, Service自动会进行动态查询
对于需要需要排序的查询加上@Order注解, 会自定按对应的属性进行分页查询

### 动态JSON视图
Json动态视图直接采用注解@Json
在控制器方法上采用@Json注解, Json注解中include 指定包含哪些字段, exclude指定排除哪些字段 如下
 ```java
 
    @Cacheable
    @RequestMapping(value = "/list", name = "分页查询活动列表")
    @Json(type = AActivity.class, exclude = "createdTime,updatedTime", include="title")
    public Result list(ActivityPageForm form) {
        Result result = Result.success();
        PageWrap<AActivity> page = activityService.page(form);
        result.putData("page", page);
        return result;
    }
```

### 采用JSR 303标准进行参数校验 
JSR 303 – Bean Validation 是一个数据验证的规范，2009 年 11 月确定最终方案。2009 年 12 月 Java EE 6 发布，Bean Validation 作为一个重要特性被包含其中。本文将对 Bean Validation 的主要功能进行介绍，并通过一些示例来演示如何在 Java 开发过程正确的使用 Bean Validation。
> Bean Validation 中的 constraint
@Null	被注释的元素必须为 null
@NotNull	被注释的元素必须不为 null
@AssertTrue	被注释的元素必须为 true
@AssertFalse	被注释的元素必须为 false
@Min(value)	被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@Max(value)	被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@DecimalMin(value)	被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@DecimalMax(value)	被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@Size(max, min)	被注释的元素的大小必须在指定的范围内
@Digits (integer, fraction)	被注释的元素必须是一个数字，其值必须在可接受的范围内
@Past	被注释的元素必须是一个过去的日期
@Future	被注释的元素必须是一个将来的日期
@Pattern(value)	被注释的元素必须符合指定的正则表达式
@Email	被注释的元素必须是电子邮箱地址
@Length	被注释的字符串的大小必须在指定的范围内
@NotEmpty	被注释的字符串的必须非空
@Range	被注释的元素必须在合适的范围内

代码示例

   ```java
   
	public class AddOrderForm implement Form {
		// 必须不为 null, 大小是 10
		@NotNull
		@Size(min = 10, max = 10)
		private String orderId;
		// 必须不为空
		@NotEmpty
		private String customer;
		// 必须是一个电子信箱地址
		@Email
		private String email;
		// 必须不为空
		@NotEmpty
		private String address;
		// 必须不为 null, 必须是下面四个字符串'created', 'paid', 'shipped', 'closed'其中之一
		// @Status 是一个定制化的 contraint
		@NotNull
		private String status;
		// 必须不为 null
		@NotNull
		private Date createDate;
		// 嵌套验证
		@Valid
		private Product product;

	}
   
```
### RequestMapping注解
请大家最好在@RequestMapping注解的name属性赋值, 方便后面自动生成接口文档, 自动抓取API接口信息写入数据库Resource表中
后期手动添加相当麻烦, 也容易出错,或者漏掉.

### Spring Cache
Spring 3.1 引入了激动人心的基于注释（annotation）的缓存（cache）技术，它本质上不是一个具体的缓存实现方案（例如 EHCache 或者 OSCache），而是一个对缓存使用的抽象，通过在既有代码中添加少量它定义的各种 annotation，即能够达到缓存方法的返回对象的效果。

例如如下对活动的分页结果进行缓存,只需要在方法上加@Cacheable注解
   ```java
   
    @Cacheable
    @RequestMapping(value = "/list", name = "分页查询活动列表")
    @Json(type = Activity.class, exclude = {"createdTime", "updatedTime"}, include="title")
    public Result list(ActivityPageForm form) {
        Result result = Result.success();
        Page<Activity> page = activityService.page(form);
        result.putData("page", page);
        return result;
    }
	
```	

------------


## 项目Service 层设计
所有的Service接口都要继承BaseService接口, BaseService定义基本的增删改查, 还有按不同的条件, 排序, 分页查询接口, 在BaseServiceImpl已经全部实现
所有其他的具体的Service都不需要写了, 
```java

public interface BaseService<T, ID extends Serializable> {
	
	/**
	 * 根据删除一个实体
	 * @param id
	 */
	public void delete(ID id);
	
	/**
	 * 根据实体对象删除一个实体
	 * @param entity
	 */
	public void delete(T entity) ;
	
	/**
	 * 根据实体集合批量删除实体
	 * @param entities
	 */
	public void delete(Iterable<? extends T> entities);
	
	/**
	 * 根据实体集合批量删除
	 * @param entities
	 */
	public void deleteInBatch(Iterable<T> entities);
	
	/**
	 * 删除所有的实体
	 */
	public void deleteAll();

	/**
	 * 分页查询
	 * @param form
	 * @return
	 */
	public PageWrap<T> page(PageForm<T> form);
	
	/**
	 * 根据ID查询一个实体(有延迟加载)
	 * @param id
	 * @return
	 */
	public T getOne(ID id);
	
	/**
	 * 根据ID查询一个实体
	 * @param id
	 * @return
	 */
	public T findOne(ID id);
	
	/**
	 * 根据ID判断一个实体是否存在
	 * @param id
	 * @return
	 */
	public boolean exists(ID id);
	
	/**
	 * 查询所有的实体
	 * @return
	 */
	public List<T> findAll() ;
	
	/**
	 * 根据ID集合查询实体集合
	 * @param ids
	 * @return
	 */
	public List<T> findAll(Iterable<ID> ids);
	
	/**
	 * 排序查询所有实体
	 * @param sort
	 * @return
	 */
	public List<T> findAll(Sort sort) ;
	
	/**
	 * 分页查询所有的实体
	 * @param pageable
	 * @return
	 */
	public Page<T> findAll(Pageable pageable);
	
	/**
	 * 按自定义条件查询一个实体
	 * @param spec
	 * @return
	 */
	public T findOne(Specification<T> spec);

	/**
	 * 按自定义条件查询所有实体
	 * @param spec
	 * @return
	 */
	public List<T> findAll(Specification<T> spec) ;
	
	/**
	 * 按自定义条件分页查询所有实体
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<T> findAll(Specification<T> spec, Pageable pageable) ;
	
	/**
	 * 根据自定义条件排序查询实体
	 * @param spec
	 * @param sort
	 * @return
	 */
	public List<T> findAll(Specification<T> spec, Sort sort);
	
	/**
	 * 统计所有实体的数量
	 * @return
	 */
	public long count() ;
	
	/**
	 * 按条件统计实体的数量
	 * @param spec
	 * @return
	 */
	public long count(Specification<T> spec) ;
	
	/**
	 * 保存后更新一个实体
	 * @param entity
	 * @return
	 */
	public <S extends T> S save(S entity) ;
	
	/**
	 * 批量保存更新实体
	 * @param entities
	 * @return
	 */
	public <S extends T> List<S> save(Iterable<S> entities);
}

```

## 项目DAO层设计

### Dao设计采用JPA2.0规范
> Java EE 5平台引入了Java持久化API（Java Persistence API，JPA），它为Java EE和Java SE应用程序提供了一个基于POJO的持久化模块。JPA处理关系数据与Java对象之间的映射，它使对象/关系（O/R）映射标准化，JPA已经被广泛采用，已经成为事实上的O/R持久化企业标准。

所有的Dao接口可以继承BaseDao接口, BaseDao定义基本的增删改查, 还有按不同的条件, 排序, 分页查询接口,还有其他各种复杂的查询接口,  在BaseDaoImpl已经全部实现
所有其他Dao接口只需要继承BaseDao ,便可以继承其全部的功能, 基本不要写其他任何代码.

### Spring Data JPA 框架
Spring DataJPA 一个最大特点, 就是直接在接口中定义查询方法，如果是符合规范的，可以不用写实现，
假如创建如下的查询：findByUsername()，框架在解析该方法时，首先剔除 findBy，然后对剩下的属性进行解析，就会根据username属性查询用户.
>目前支持的关键字写法如下：

| Keyword | Sample | JPQL snippet |
| ------------ | ------------ | ------------ |
| And |findByLastnameAndFirstname|… where x.lastname = ?1 and x.firstname = ?2|
| Or | findByLastnameOrFirstname | … where x.lastname = ?1 or x.firstname = ?2|
| Between | findByStartDateBetween | … where x.startDate between 1? and ?2|
| LessThan| findByAgeLessThan| … where x.age < ?1|
|GreaterThan| findByAgeGreaterThan|… where x.age > ?1|
|IsNull|findByAgeIsNull|… where x.age is null|
|IsNotNull,NotNull|findByAge(Is)NotNull|… where x.age not null|
|Like|findByFirstnameLike|… where x.firstname like ?1|
|NotLike|findByFirstnameNotLike|… where x.firstname not like ?1|
|OrderBy|findByAgeOrderByLastnameDesc|… where x.age = ?1 order by x.lastname desc|
|Not|findByLastnameNot|… where x.lastname <> ?1|
|In|findByAgeIn(Collection<Age> ages)|… where x.age in ?1|
|NotIn|findByAgeNotIn(Collection<Age> age)|… where x.age not in ?1|
	
------------

### 自定义SQL查询

```java
public interface ActivityDao extends BaseDao<Long, Activity>{
    
    /**
     * 查询整个实体
     */
    @Query(value = "select * from activity where title = :title", nativeQuery = true)
    Activity findByTitle(@Param("title) String title);
    
    /**
     * 查询实体部分字段或者进行复杂的链接查询， 子查询等， 可以用DTO接口返回结果
     */
    @NativeQuery("select a.id, a.title, a.username from ativity a join user u on a.user_id = u.id where a.title = :title")
    ActivityDTO findByTitle(@Param("title") String title)

}
```


##项目model层设计
所有实体类全部集成BaseEntity类, 实体类写法完全遵循JPA2.0规范
这个可以尽可能写SQL语句
>代码示例

```java
@Entity
@Table(name = "activity")
public class Activity extends BaseEntity<Activity> {

	private static final long serialVersionUID = -3216080915989774115L;

	@Id
	private Long id;

	private String title;

	private String img;

	private Short status;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;
}
```








