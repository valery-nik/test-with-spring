
package com.testwithspring.intermediate.web;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.testwithspring.intermediate.IntegrationTest;
import com.testwithspring.intermediate.IntegrationTestContext;
import com.testwithspring.intermediate.ReplacementDataSetLoader;
import com.testwithspring.intermediate.Tasks;
import com.testwithspring.intermediate.config.Profiles;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestContext.class})
//@ContextConfiguration(locations = {"classpath:integration-test-context.xml"})
@WebAppConfiguration
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        ServletTestExecutionListener.class
})
@DatabaseSetup("task.xml")
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@Category(IntegrationTest.class)
@ActiveProfiles(Profiles.INTEGRATION_TEST)
public class ProcessUpdateTaskFormWhenValidationIsSuccessfulTest {

    private static final String FEEDBACK_MESSAGE_TASK_CREATED = "The information of a task was updated successfully.";
    private static final String FLASH_MESSAGE_KEY_FEEDBACK = "feedbackMessage";

    private static final String MODEL_ATTRIBUTE_TASK_ID = "taskId";

    private static final String NEW_DESCRIPTION = "The old lesson was not good";
    private static final String NEW_TITLE = "Rewrite an existing lesson";

    private static final String TASK_PROPERTY_NAME_DESCRIPTION = "description";
    private static final String TASK_PROPERTY_NAME_ID = "id";
    private static final String TASK_PROPERTY_NAME_TITLE = "title";

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Before
    public void configureSystemUnderTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .build();
    }

    @Test
    public void shouldReturnHttpStatusCodeFound() throws Exception {
        submitUpdateTaskForm()
                .andExpect(status().isFound());
    }

    @Test
    public void shouldRedirectUserToViewTaskView() throws Exception {
        submitUpdateTaskForm()
                .andExpect(view().name("redirect:/task/{taskId}"))
                .andExpect(model().attribute(MODEL_ATTRIBUTE_TASK_ID, is(Tasks.WriteLesson.ID.toString())));
    }

    @Test
    public void shouldRedirectUserToViewTaskPageUrl() throws Exception {
        submitUpdateTaskForm()
                .andExpect(redirectedUrl("/task/2"));
    }

    @Test
    public void shouldAddFeedbackMessageAsAFlashAttribute() throws Exception {
        submitUpdateTaskForm()
                .andExpect(flash().attribute(FLASH_MESSAGE_KEY_FEEDBACK, FEEDBACK_MESSAGE_TASK_CREATED));
    }

    @Test
    @ExpectedDatabase(value = "update-task-should-update-task.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void shouldUpdateTheInformationOfTask() throws Exception {
        submitUpdateTaskForm();
    }

    private ResultActions submitUpdateTaskForm() throws Exception {
        return  mockMvc.perform(post("/task/{taskId}/update", Tasks.WriteLesson.ID)
                .param(TASK_PROPERTY_NAME_DESCRIPTION, NEW_DESCRIPTION)
                .param(TASK_PROPERTY_NAME_ID, Tasks.WriteLesson.ID.toString())
                .param(TASK_PROPERTY_NAME_TITLE, NEW_TITLE)
        );
    }
}