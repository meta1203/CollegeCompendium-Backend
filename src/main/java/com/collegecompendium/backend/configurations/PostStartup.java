package com.collegecompendium.backend.configurations;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.repositories.CollegeRepository;

import jakarta.annotation.PostConstruct;

@Component
public class PostStartup {
	@Autowired
	private CollegeRepository collegeRepository;

	@Profile("dev")
	@PostConstruct
	public void init() {
		// prepopulate colleges
		College c1 = College.builder()
				.name("New Mexico Tech")
				.inStateCost(30000)
				.outStateCost(40000)
				.location(new Location(
						"801 Leroy Pl, Socorro, NM 87801",
						"34.06609123582969",
						"-106.9056496898088"
						))
				.description("The first test college")
				.photos(Arrays.asList(
						"https://www.nmt.edu/_resources/img/photos/heroimages/NMTGenericHero.png",
						"https://www.nmt.edu/advancement/images/Drone_Shot_Campus_August2021_large.jpg"))
				// .phoneNumber("5758355133")
				.popularity(69)
				.url("https://www.nmt.edu/")
				.build();
		c1 = collegeRepository.save(c1);

		College c2 = College.builder()
				.name("Dirty Dave's Degrees and Dissertations")
				.inStateCost(100)
				.outStateCost(125)
				.location(new Location(
						"104 Neel St, Socorro, NM 87801",
						"34.059078845885246",
						"-106.89836091130815"
						))
				.description("The second test college")
				.photos(Arrays.asList(
						"http://www.hrwiki.org/w/images/f/f5/CGNU_Crest.png",
						"http://www.hrwiki.org/w/images/e/e2/Sbemail178_screenshot.PNG"
						))
				// .phoneNumber("5059800370")
				.popularity(1)
				.url("http://cgnuonline-eniversity.edu")
				.build();
		c2 = collegeRepository.save(c2);

		College c3 = College.builder()
				.name("University of New Mexico")
				.inStateCost(900000)
				.outStateCost(100000000)
				.location(new Location(
						"1 University of New Mexico, Albuquerque NM 87131",
						"35.084397617922576",
						"-106.61977049166329"
						))
				.description("The third test college")
				.photos(Arrays.asList(
						"https://s3.amazonaws.com/cms.ipressroom.com/175/files/20198/5d76bea32cfac25e55abafe4_UNM+Lobo+with+Scholes/UNM+Lobo+with+Scholes_hero.jpg",
						"https://www.unm.edu/features/2022/smith-plaza.jpg"
						))
				//.phoneNumber("5052778900")
				.popularity(420)
				.url("https://unm.edu")
				.build();
		c3 = collegeRepository.save(c3);
	}
}
