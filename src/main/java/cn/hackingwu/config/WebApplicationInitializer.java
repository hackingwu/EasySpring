package cn.hackingwu.config;

import cn.hackingwu.filter.CorsFilter;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * @author hackingwu.
 * @since 2015/8/17
 */
@Order(1)
public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{MongoConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{EasyWebMvcConfigurerAdapter.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void registerDispatcherServlet(ServletContext servletContext) {
        super.registerDispatcherServlet(servletContext);
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        this.addFilter(servletContext, "characterEncodingFilter", characterEncodingFilter);
        this.addFilter(servletContext,"corsFilter",new CorsFilter());
    }

    private void addFilter(ServletContext servletContext,String filterName,Filter filter){
        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(filterName, filter);
        filterRegistration.setAsyncSupported(super.isAsyncSupported());
        filterRegistration.addMappingForUrlPatterns(this.getDispatcherTypes(),false,new String[]{"/*"});
    }

    private EnumSet<DispatcherType> getDispatcherTypes(){
        return super.isAsyncSupported() ?
                EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD,DispatcherType.INCLUDE,DispatcherType.ASYNC)
                :
                EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD,DispatcherType.INCLUDE);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
    }




}
