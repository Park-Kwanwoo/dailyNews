package dev.park.dailynews.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringBootTest
@Retention(RetentionPolicy.RUNTIME)
@TestExecutionListeners(value = {CustomTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface DailyTest {
}
