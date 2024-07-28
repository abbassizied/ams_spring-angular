package com.sip.ams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sip.ams.entities.Provider;
import com.sip.ams.repositories.ProviderRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProviderRepositoryTest {

	@Autowired
	private ProviderRepository providerRepository;

	@Test
	public void testCreateProvider() {
		Provider p1 = new Provider("Med Amine MEZGHICH", "DevOps Tools Training", "amine@gmail.com");

		providerRepository.save(p1);

		Long savedProviderID = p1.getId();

		Provider provider = providerRepository.findById(savedProviderID).orElseThrow();

		assertEquals(savedProviderID, provider.getId());
		assertEquals("Med Amine MEZGHICH", provider.getName());
		assertEquals("amine@gmail.com", provider.getEmail());

	}

	@Test
	public void testListProviders() {

		Provider p1 = new Provider("Koré sud", "Samsung Galaxy", "samsung@hotmail.com");
		Provider p2 = new Provider("USA Amercia", "HP Pavillon", "hp.pavillon@hotmail.com");
		Provider p3 = new Provider("Tunisia", "Aziza", "aziza@gmail.com");
		Provider p4 = new Provider("Japon", "Toshiba", "toshiba@gmail.com");

		providerRepository.saveAll(List.of(p1, p2, p3, p4));

		List<Provider> result = (List<Provider>) providerRepository.findAll();
		assertThat(result).isNotEmpty();
	}

	@Test
	public void testUpdateProvider() {

		Provider p1 = new Provider("Med Amine MEZGHICH", "DevOps Tools Training", "amine@gmail.com");

		providerRepository.save(p1);

		p1.setEmail("aminos1234@gmail.com");

		// update
		providerRepository.save(p1);

		Long updatedProviderID = p1.getId();
		Optional<Provider> result = providerRepository.findById(updatedProviderID);

		assertEquals(true, result.isPresent());

		Provider provider = result.get();
		assertNotNull(provider.getId());
		assertTrue(provider.getId() > 0);

		assertEquals("Med Amine MEZGHICH", provider.getName());
		assertEquals("aminos1234@gmail.com", provider.getEmail());

	}

	@Test
	public void testDeleteProviderById() {

		Provider p1 = new Provider("Med Amine MEZGHICH", "DevOps Tools Training", "amine@gmail.com");

		providerRepository.save(p1);

		Long savedProviderID = p1.getId();
		providerRepository.deleteById(savedProviderID);

		Optional<Provider> result = providerRepository.findById(savedProviderID);
		assertTrue(result.isEmpty());

	}

}
