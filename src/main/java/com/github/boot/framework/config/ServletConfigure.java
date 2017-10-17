package com.github.boot.framework.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.boot.framework.support.spring.ApplicationContextUtils;
import com.github.boot.framework.web.interceptor.ResubmitInterceptor;
import com.github.boot.framework.web.listener.ContextLoadedListener;
import com.github.boot.framework.web.result.ReturnJsonHandler;
import com.github.boot.framework.web.exception.GlobalExceptionHandler;
import com.github.boot.framework.web.interceptor.AccessLimitInterceptor;
import com.github.boot.framework.web.interceptor.OAuthInterceptor;
import com.github.boot.framework.web.resolver.FormArgumentResolver;
import com.github.boot.framework.web.resolver.SessionArgumentResolver;
import com.github.boot.framework.web.result.ResultJsonSerializer;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @Description web应用配置
 * @author cjh
 * @version 1.0
 * @date：2017年2月15日 下午6:29:49
 */
@EnableAsync
@Configuration
@EnableWebMvc
public class ServletConfigure extends WebMvcConfigurerAdapter implements ApplicationContextAware{

	@Value("${web.cors.domains:*}")
	private String corsDomains;

	private ApplicationContext applicationContext;

	private List<HttpMessageConverter<?>> converters;

	@Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
        return container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
            container.addErrorPages(new ErrorPage(Throwable.class,"/error/500"));
            container.addErrorPages(new ErrorPage(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "/error/500"));
            container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/500"));
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/500"));
        };
    }

	/**
	 * 全局异常处理器
	 * @param errorAttributes
	 * @return
	 */
	@Bean
	@SuppressWarnings("SpringJavaAutowiringInspection")
    public GlobalExceptionHandler globalExceptionHandler(ErrorAttributes errorAttributes){
    	return new GlobalExceptionHandler(errorAttributes);
	}

	/**
	 * 容器加载监听器
	 * @return
	 */
	@Bean
	public ContextLoadedListener contextLoadedListener(){
		return new ContextLoadedListener();
	}

	/**
	 * 添加拦截器
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new OAuthInterceptor());
		registry.addInterceptor(new ResubmitInterceptor());
		registry.addInterceptor(new AccessLimitInterceptor(applicationContext.getBean(RedissonClient.class)));
		Map<String, HandlerInterceptorAdapter> interceptors = applicationContext.getBeansOfType(HandlerInterceptorAdapter.class);
		for (HandlerInterceptorAdapter interceptor : interceptors.values()){
			registry.addInterceptor(interceptor);
		}
	}

	/**
	 * 注册参数解析器
	 * @param argumentResolvers
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		HandlerMethodArgumentResolver formResolver = new FormArgumentResolver(converters);
		HandlerMethodArgumentResolver sessionResolver = new SessionArgumentResolver();
		argumentResolvers.add(formResolver);
		argumentResolvers.add(sessionResolver);
	}

	/**
	 * 注册数据转换器
	 * @param converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
		jsonHttpMessageConverter.setObjectMapper(new ResultJsonSerializer());
		converters.add(jsonHttpMessageConverter);
		ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
		byteArrayHttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		XmlMapper xmlMapper = new XmlMapper(module);
		xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);//设置序列化不包含Java对象中为空的属性
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MappingJackson2XmlHttpMessageConverter xmlMessageConverter = new MappingJackson2XmlHttpMessageConverter();
		xmlMessageConverter.setObjectMapper(xmlMapper);
		converters.add(byteArrayHttpMessageConverter);
		converters.add(stringHttpMessageConverter);
		converters.add(xmlMessageConverter);
		this.converters = converters;
	}

	/**
	 * 注册返回结果处理器
	 * @param returnValueHandlers
	 */
	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		HandlerMethodReturnValueHandler handler = new ReturnJsonHandler();
		returnValueHandlers.add(handler);
	}


	/**
	 * 添加跨域请求配置
	 * @param registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedHeaders("x-requested-with", "Authorization")
				.allowedMethods("GET", "POST", "OPTIONS", "DELETE", "PUT")
				.allowedOrigins(corsDomains.split(","))
				.allowCredentials(true)
				.maxAge(3600);
	}

	/**
	 * 添加静态资源处理器
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		super.addResourceHandlers(registry);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		ApplicationContextUtils.setContext(applicationContext);
	}
}
