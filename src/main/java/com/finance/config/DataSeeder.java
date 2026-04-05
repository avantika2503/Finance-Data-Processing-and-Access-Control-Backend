package com.finance.config;

import com.finance.model.FinancialRecord;
import com.finance.model.User;
import com.finance.model.enums.RecordType;
import com.finance.model.enums.Role;
import com.finance.model.enums.UserStatus;
import com.finance.repository.FinancialRecordRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void run(String... args) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> seedIfEmpty());
    }

    private void seedIfEmpty() {
        if (userRepository.count() > 0) {
            log.debug("Skipping seed: users table is not empty");
            return;
        }

        User admin = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@test.com")
                .password(passwordEncoder.encode(DEMO_PASSWORD))
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build());

        User analyst = userRepository.save(User.builder()
                .name("Analyst User")
                .email("analyst@test.com")
                .password(passwordEncoder.encode(DEMO_PASSWORD))
                .role(Role.ANALYST)
                .status(UserStatus.ACTIVE)
                .build());

        User viewer = userRepository.save(User.builder()
                .name("Viewer User")
                .email("viewer@test.com")
                .password(passwordEncoder.encode(DEMO_PASSWORD))
                .role(Role.VIEWER)
                .status(UserStatus.ACTIVE)
                .build());

        List<FinancialRecord> records = new ArrayList<>();
        records.add(record(admin, 5200.0, RecordType.INCOME, "Salary", LocalDate.of(2026, 1, 1), "January salary"));
        records.add(record(admin, 120.50, RecordType.EXPENSE, "Food", LocalDate.of(2026, 1, 3), "Groceries"));
        records.add(record(analyst, 800.0, RecordType.INCOME, "Freelance", LocalDate.of(2026, 1, 10), "Side project"));
        records.add(record(admin, 1400.0, RecordType.EXPENSE, "Rent", LocalDate.of(2026, 1, 5), "Monthly rent"));
        records.add(record(viewer, 45.0, RecordType.EXPENSE, "Transport", LocalDate.of(2026, 1, 12), "Transit pass"));
        records.add(record(analyst, 200.0, RecordType.EXPENSE, "Utilities", LocalDate.of(2026, 1, 15), "Electric bill"));
        records.add(record(admin, 5200.0, RecordType.INCOME, "Salary", LocalDate.of(2026, 2, 1), "February salary"));
        records.add(record(admin, 95.0, RecordType.EXPENSE, "Food", LocalDate.of(2026, 2, 4), "Dining out"));
        records.add(record(analyst, 150.0, RecordType.EXPENSE, "Entertainment", LocalDate.of(2026, 2, 14), "Concert"));
        records.add(record(admin, 300.0, RecordType.INCOME, "Investment", LocalDate.of(2026, 2, 20), "Dividend"));
        records.add(record(admin, 1400.0, RecordType.EXPENSE, "Rent", LocalDate.of(2026, 2, 5), "Rent"));
        records.add(record(viewer, 60.0, RecordType.EXPENSE, "Transport", LocalDate.of(2026, 2, 18), "Fuel"));
        records.add(record(analyst, 5200.0, RecordType.INCOME, "Salary", LocalDate.of(2026, 3, 1), "March salary"));
        records.add(record(admin, 250.0, RecordType.EXPENSE, "Healthcare", LocalDate.of(2026, 3, 8), "Pharmacy"));
        records.add(record(analyst, 1200.0, RecordType.INCOME, "Freelance", LocalDate.of(2026, 3, 12), "Consulting"));
        records.add(record(admin, 180.0, RecordType.EXPENSE, "Food", LocalDate.of(2026, 3, 15), "Groceries"));
        records.add(record(viewer, 35.0, RecordType.EXPENSE, "Food", LocalDate.of(2026, 3, 20), "Coffee"));
        records.add(record(admin, 90.0, RecordType.EXPENSE, "Utilities", LocalDate.of(2026, 3, 22), "Internet"));

        recordRepository.saveAll(records);
        log.info("Seeded demo users admin@test.com, analyst@test.com, viewer@test.com and {} financial records",
                records.size());
    }

    private static FinancialRecord record(User createdBy, double amount, RecordType type, String category,
                                          LocalDate date, String description) {
        return FinancialRecord.builder()
                .amount(amount)
                .type(type)
                .category(category)
                .entryDate(date)
                .description(description)
                .isDeleted(false)
                .createdBy(createdBy)
                .build();
    }
}
