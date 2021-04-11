package org.richmond.spring.webmvc;

// Tests for server-side code that handles HTTP client requests

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import static org.assertj.core.api.Assertions.assertThat;

public class ControllerTests {

    @Test
    public void parameterizableViewController() throws Exception {

        String viewName = "viewName";
        ParameterizableViewController pvc = new ParameterizableViewController();
        pvc.setViewName(viewName);
        // We don't care about the params.
        ModelAndView mv = pvc.handleRequest(new MockHttpServletRequest("GET", "foo.html"),
                new MockHttpServletResponse());
        assertThat(mv.getModel().size() == 0).as("model has no data").isTrue();
        assertThat(mv.getViewName().equals(viewName)).as("model has correct viewname").isTrue();
        assertThat(pvc.getViewName().equals(viewName)).as("getViewName matches").isTrue();
    }
}
