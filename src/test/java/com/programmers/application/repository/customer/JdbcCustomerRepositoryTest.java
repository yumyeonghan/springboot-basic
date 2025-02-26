package com.programmers.application.repository.customer;

import com.programmers.application.domain.customer.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JdbcCustomerRepositoryTest {

    @Autowired
    private JdbcCustomerRepository jdbcCustomerRepository;

    @DisplayName("옳바른 이름과 이메일 입력 시, insert()를 실행하면 테스트가 성공한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "aCustomer, mgtmh991013@naver.com",
            "bCustomer, mgtmh991013@gmail.com"
    })
    void insert(String name, String email) {
        //given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, name, email);

        //when
        jdbcCustomerRepository.insert(customer);

        //then
        Customer savedCustomer = jdbcCustomerRepository.findByCustomerId(customer.getCustomerId()).get();
        assertThat(customer).usingRecursiveComparison().isEqualTo(savedCustomer);
    }

    @DisplayName("Customer 생성 및 저장 시, findByEmail()을 실행하면 해당 이메일로 Customer이 조회된다.")
    @ParameterizedTest
    @CsvSource(value = {
            "aCustomer, mgtmh991013@naver.com",
            "bCustomer, mgtmh991013@gmail.com"
    })
    void findByEmail(String name, String email) {
        //given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, name, email);
        jdbcCustomerRepository.insert(customer);

        //when
        Customer savedCustomer = jdbcCustomerRepository.findByEmail(email).get();

        //then
        assertThat(customer).usingRecursiveComparison().isEqualTo(savedCustomer);
    }

    @DisplayName("Customer 생성 및 저장 시, finalAll() 실행하면 전체 Customer가 조회된다.")
    @Test
    void findAll() {
        final int expectedCount = 2;
        //given
        Customer customer1 = new Customer(UUID.randomUUID(), "aCustomer", "mgtmh991013@naver.com");
        Customer customer2 = new Customer(UUID.randomUUID(), "bCustomer", "mgtmh991013@gmail.com");
        ArrayList<Customer> customers = new ArrayList<>(List.of(customer1, customer2));
        createAndSaveCustomer(customers);

        //when
        List<Customer> customerList = jdbcCustomerRepository.findAll();

        //then
        assertThat(customerList).hasSize(expectedCount);
    }

    private void createAndSaveCustomer(List<Customer> customers) {
        for (Customer customer : customers) {
            jdbcCustomerRepository.insert(customer);
        }
    }

    @DisplayName("Customer 상태를 변경하고, update() 실행하면 Customer가 변경된다.")
    @Test
    void update() {
        //given
        Customer customer = new Customer(UUID.randomUUID(), "aCustomer", "mgtmh991013@naver.com");
        jdbcCustomerRepository.insert(customer);
        customer.changeName("bCustomer");
        customer.login();

        //when
        jdbcCustomerRepository.update(customer);

        //then
        Customer updatedCustomer = jdbcCustomerRepository.findByCustomerId(customer.getCustomerId()).get();
        assertThat(updatedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(updatedCustomer.getLastLoginAt()).isEqualTo(customer.getLastLoginAt());
    }

    @DisplayName("Customer 생성 및 저장 시, deleteByCustomerId() 실행하면 Customer가 조회되지 않는다.")
    @Test
    void deleteByCustomerId() {
        //given
        Customer customer = new Customer(UUID.randomUUID(), "aCustomer", "mgtmh991013@naver.com");
        jdbcCustomerRepository.insert(customer);

        //when
        jdbcCustomerRepository.deleteByCustomerId(customer.getCustomerId());

        //then
        Optional<Customer> deletedCustomer = jdbcCustomerRepository.findByCustomerId(customer.getCustomerId());
        assertThat(deletedCustomer).isEmpty();
    }

    @DisplayName("Customer를 삭제하고, findDeletedCustomerByCustomerId() 실행하면 삭제된 Customer가 조회된다.")
    @Test
    void findDeletedCustomerByCustomerId() {
        //given
        Customer customer = new Customer(UUID.randomUUID(), "aCustomer", "mgtmh991013@naver.com");
        jdbcCustomerRepository.insert(customer);
        jdbcCustomerRepository.deleteByCustomerId(customer.getCustomerId());

        //when
        Customer deletedCustomer = jdbcCustomerRepository.findDeletedCustomerByCustomerId(customer.getCustomerId()).get();

        //then
        assertThat(deletedCustomer)
                .usingRecursiveComparison()
                .isEqualTo(customer);
    }

    @DisplayName("저장 되지 않은 Customer의 Id로 findByCustomerId() 실행하면 빈 Optional이 반환된다.")
    @Test
    void returnEmptyOptional() {
        //given
        UUID notSavedCustomerId = UUID.randomUUID();

        //when
        Optional<Customer> notSavedCustomer = jdbcCustomerRepository.findByCustomerId(notSavedCustomerId);

        //then
        assertThat(notSavedCustomer).isEmpty();
    }
}
