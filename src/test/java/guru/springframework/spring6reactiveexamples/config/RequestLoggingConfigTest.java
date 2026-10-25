package guru.springframework.spring6reactiveexamples.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.server.WebFilter;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingConfigTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withUserConfiguration(RequestLoggingConfig.class);

	@Test
	void registersFilterByDefault() {
		contextRunner.run(context -> assertThat(context).hasBean("requestLoggingWebFilter"));
	}

	@Test
	void doesNotRegisterFilterWhenDisabled() {
		contextRunner.withPropertyValues("appconfig.request-logging.enabled=false")
			.run(context -> assertThat(context).doesNotHaveBean(WebFilter.class));
	}

	@Test
	void bindsExcludedPathsFromConfiguration() {
		contextRunner.withPropertyValues("appconfig.request-logging.exclude-paths[0]=/actuator/health").run(context -> {
			RequestLoggingConfig.RequestLoggingProperties properties = context
				.getBean(RequestLoggingConfig.RequestLoggingProperties.class);

			assertThat(properties.getExcludePaths()).containsExactly("/actuator/health");
			assertThat(RequestLoggingWebFilter.isExcluded("/actuator/health/readiness", properties.getExcludePaths()))
				.isTrue();
			assertThat(RequestLoggingWebFilter.isExcluded("/api/v1/beer", properties.getExcludePaths())).isFalse();
		});
	}

}
