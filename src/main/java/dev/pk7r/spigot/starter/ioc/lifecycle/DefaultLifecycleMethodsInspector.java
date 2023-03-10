package dev.pk7r.spigot.starter.ioc.lifecycle;

import dev.pk7r.spigot.starter.ioc.exception.LifecycleException;
import dev.pk7r.spigot.starter.ioc.factory.bean.BeanFactory;
import dev.pk7r.spigot.starter.ioc.util.ReflectionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.pacesys.reflect.Reflect;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class DefaultLifecycleMethodsInspector implements LifecycleMethodsInspector {

    private final Set<Method> postConstructMethods = new HashSet<>();

    private final Set<Method> preDestroyMethods = new HashSet<>();

    private BeanFactory beanFactory;

    @Override
    public void registerLifecycleMethods(Class<?> clazz) {
        val methods = ReflectionUtil.getMethods(Reflect.MethodType.INSTANCE, clazz,
                PostConstruct.class, PreDestroy.class);
        methods.forEach(method -> {
            if (method.getParameterCount() > 0) {
                throw new LifecycleException("Lifecycle methods can not contains parameters");
            }
            if (!method.getReturnType().equals(Void.TYPE)) {
                throw new LifecycleException("Lifecycle methods must return void");
            }
            if (method.isAnnotationPresent(PostConstruct.class)) getPostConstructMethods().add(method);
            if (method.isAnnotationPresent(PreDestroy.class)) getPreDestroyMethods().add(method);
        });
    }

    @Override
    public void destroyMethods() {
        getPreDestroyMethods().clear();
        getPostConstructMethods().clear();
    }

    @Override
    @SneakyThrows
    public void invokeLifecycleMethod(Method method) {
        method.setAccessible(true);
        method.invoke(method.getDeclaringClass().newInstance());
        method.setAccessible(false);
    }
}