package at.musik.config;

import at.musik.service.util.BufferedImageThumbnailer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

import static reactor.event.selector.Selectors.$;

@Configuration
//@EnableReactor
public class ImageConfiguration {

//    @Bean
//    public Reactor reactor(Environment env) {
//        Reactor reactor = Reactors.reactor(env, Environment.THREAD_POOL);
//
//        // Register our thumbnailer on the Reactor
//        reactor.receive($("thumbnail"), new BufferedImageThumbnailer(250));
//        reactor.receive($("origin"), new BufferedImageThumbnailer(-1));
//
//        return reactor;
//    }
}
