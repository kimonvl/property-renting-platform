package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.static_data.Language;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepo extends JpaRepository<@NonNull Language, @NonNull Long> {
    List<Language> findByCodeInIgnoreCase(List<String> codes);
}
