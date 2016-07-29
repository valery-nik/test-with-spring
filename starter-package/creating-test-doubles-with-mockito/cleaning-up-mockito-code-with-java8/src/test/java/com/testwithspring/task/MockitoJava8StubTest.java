package com.testwithspring.task;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.testwithspring.task.TestDoubles.dummy;
import static com.testwithspring.task.TestDoubles.stub;
import static info.solidsoft.mockito.java8.LambdaMatcher.argLambda;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * This test class demonstrates how we can clean up our
 * stubbing code by using the {@code Mockito} class and Java 8.
 */
public class MockitoJava8StubTest {

    private static final Long CREATOR_ID = 1L;
    private static final String TASK_DESCRIPTION = "description";
    private static final String TASK_TITLE = "title";

    private TaskRepository repository;

    @Before
    public void createStub() {
        repository = stub(TaskRepository.class);
    }

    /**
     * This test method demonstrates how we can create an argument
     * matcher by using a lambda expression. This is be useful if
     * we want to stub method behavior and specify "complex" conditions
     * for the expected method parameter.
     */
    @Test
    public void shouldReturnObjectWhenParameterIsMatchedWithLambdaMatcher() {
        Task saved = dummy(Task.class);
        when(repository.save(argLambda(task -> task.getId() == null))).thenReturn(saved);

        Task created = new TaskBuilder()
                .withCreator(CREATOR_ID)
                .withTitle(TASK_TITLE)
                .withDescription(TASK_DESCRIPTION)
                .build();
        Task actual = repository.save(created);

        assertThat(actual).isSameAs(saved);
    }

    /**
     * This test demonstrates how we can remove boilerplate code
     * and create returned {@code Answer} object by using a lambda
     * expression.
     */
    @Test
    public void shouldReturnObjectByUsingAnswer() {
        Task expected = dummy(Task.class);
        when(repository.findById(1L)).thenAnswer(invocation -> {
            Long idParameter = (Long) invocation.getArguments()[0];
            if (idParameter.equals(1L)) {
                return Optional.of(expected);
            }
            else {
                return Optional.empty();
            }
        });

        Optional<Task> actual = repository.findById(1L);

        assertThat(actual).containsSame(expected);
    }
}
