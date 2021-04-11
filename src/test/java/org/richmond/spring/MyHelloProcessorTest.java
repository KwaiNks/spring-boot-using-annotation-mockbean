package org.richmond.spring;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyHelloProcessorTest {
    @Autowired
    private MyHelloProcessor myHelloProcessor;

    @MockBean
    private HelloService helloService;

    @Test
    public void testSayHi() {
        Mockito.when(helloService.getMessage()).thenReturn("test message.");
        String message = myHelloProcessor.getMessage();
        Assert.assertEquals(message, "test message.");
    }
}