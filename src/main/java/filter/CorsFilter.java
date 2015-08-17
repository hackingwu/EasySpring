package filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hackingwu.
 * @since 2015/8/17
 */
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.setHeader("Access-Controller-Allow-Origin","*");
        httpServletResponse.setHeader("Access-Controller-Allow-Methods","GET, POST, DELETE, PUT, PATCH, OPTIONS, TRACE, HEAD");
        httpServletResponse.setHeader("Access-Controller-Allow-Headers","Origin, Accept, X-Requested-With, Content-Type, Access-Controller-Request-Headers, Authorization");
        httpServletResponse.setHeader("Access-Controller-Max-Age","1800");
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
