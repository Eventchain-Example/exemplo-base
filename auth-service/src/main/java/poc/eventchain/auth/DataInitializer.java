package poc.eventchain.auth;


import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poc.eventchain.auth.model.User;
import poc.eventchain.auth.model.UserRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserRepository users;

	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {

		this.users.save(User.builder().username("user").password(this.passwordEncoder.encode("password"))
				.roles(Arrays.asList("ROLE_USER")).build());

		this.users.save(User.builder().username("admin").password(this.passwordEncoder.encode("password"))
				.roles(Arrays.asList("ROLE_USER", "ROLE_ADMIN")).build());

		log.debug("printing all users...");
		this.users.findAll().forEach(u -> log.debug(" User :" + u.toString()));
	}
}
