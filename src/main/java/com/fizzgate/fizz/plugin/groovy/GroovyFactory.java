package com.fizzgate.fizz.plugin.groovy;

import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import com.fizzgate.Fizz;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author huanghua
 */
@Slf4j
public class GroovyFactory {

    private static final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    private static final ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();
    private static final GroovyFactory GROOVY_FACTORY = new GroovyFactory();

    public static GroovyFactory getInstance() {
        return GROOVY_FACTORY;
    }

    /**
     * load new instance, prototype
     *
     * @param codeSource
     * @return
     * @throws Exception
     */
    public IHandler loadNewInstance(String codeSource) throws Exception {
        if (StringUtils.isNotBlank(codeSource)) {
            Class<?> clazz = getCodeSourceClass(codeSource);
            if (clazz != null) {
                Object instance = BeanUtils.instantiateClass(clazz);
                if (instance instanceof IHandler) {
                    this.injectService(instance);
                    return (IHandler) instance;
                } else {
                    throw new IllegalArgumentException("loadNewInstance error, "
                            + "cannot convert from instance[" + instance.getClass() + "] to IJobHandler");
                }
            }
        }
        throw new IllegalArgumentException("loadNewInstance error, instance is null");
    }

    private Class<?> getCodeSourceClass(String codeSource) {
        try {
            String md5Str = DigestUtils.md5Hex(codeSource);
            Class<?> clazz = CLASS_CACHE.get(md5Str);
            if (clazz == null) {
                clazz = groovyClassLoader.parseClass(codeSource);
                CLASS_CACHE.putIfAbsent(md5Str, clazz);
            }
            return clazz;
        } catch (Exception e) {
            return groovyClassLoader.parseClass(codeSource);
        }
    }

    /**
     * inject service of bean field
     *
     * @param instance
     */
    public void injectService(Object instance) {
        ApplicationContext appContext = Fizz.context;
        try {
            if (instance == null || appContext == null) {
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        appContext.getAutowireCapableBeanFactory().autowireBean(instance);
//        Field[] fields = instance.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            if (Modifier.isStatic(field.getModifiers())) {
//                continue;
//            }
//
//            Object fieldBean = null;
//            // with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired
//            if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
//                try {
//                    Resource resource = AnnotationUtils.getAnnotation(field, Resource.class);
//                    if (resource != null && resource.name().length() > 0) {
//                        fieldBean = appContext.getBean(resource.name());
//                    } else {
//                        fieldBean = appContext.getBean(field.getName());
//                    }
//                } catch (Exception e) {
//                }
//                if (fieldBean == null) {
//                    fieldBean = appContext.getBean(field.getType());
//                }
//            } else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
//                Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
//                if (qualifier != null && qualifier.value().length() > 0) {
//                    fieldBean = appContext.getBean(qualifier.value());
//                } else {
//                    fieldBean = appContext.getBean(field.getType());
//                }
//            }
//
//            if (fieldBean != null) {
//                field.setAccessible(true);
//                try {
//                    field.set(instance, fieldBean);
//                } catch (IllegalArgumentException | IllegalAccessException e) {
//                    log.error(e.getMessage(), e);
//                }
//            }
//        }
    }

}
