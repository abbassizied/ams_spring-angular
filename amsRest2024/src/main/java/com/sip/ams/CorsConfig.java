// package com.sip.ams;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public WebMvcConfigurer corsConfigurer() {
//         return new WebMvcConfigurer() {
//             @Override
//             public void addCorsMappings(CorsRegistry registry) {
//                 registry.addMapping("/**")
//                         .allowedOrigins("http://192.168.59.102:30150")  // Ajoutez votre origine spécifique
//                         .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                         .allowedHeaders("*")
//                         .allowCredentials(true);
//             }
//         };
//     }
// }
