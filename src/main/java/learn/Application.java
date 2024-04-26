package learn;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import learn.controller.HelloController;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        HelloController controller = context.getBean("helloController", HelloController.class);
        System.out.println(controller.getHelloService());
        System.out.println(controller);
    }
}
