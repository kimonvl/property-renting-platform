package gr.aueb.cf.property_renting_platform.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ContentTypeLogFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ContentTypeLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getRequestURI().contains("/partner/apartment/addApartment")) {
            log.info(">>> {} {} Content-Type={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getContentType());
        }

        chain.doFilter(request, response);
    }
}
