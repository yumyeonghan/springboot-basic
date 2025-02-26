package com.programmers.application.service.voucher;

import com.programmers.application.domain.voucher.VoucherType;
import com.programmers.application.dto.reponse.VoucherInfoResponse;
import com.programmers.application.dto.request.RequestFactory;
import com.programmers.application.dto.request.VoucherCreationRequest;
import com.programmers.application.exception.NotFoundResourceException;
import com.programmers.application.repository.voucher.MemoryVoucherRepository;
import com.programmers.application.repository.voucher.VoucherRepository;
import com.programmers.application.service.VoucherService;
import com.programmers.application.service.VoucherServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VoucherServiceImplTest {
    private static final int FIXED_DISCOUNT_AMOUNT = 100;
    private static final int PERCENT_DISCOUNT_AMOUNT = 10;
    private static final String FIXED_AMOUNT_VOUCHER_TYPE = "fixed";
    private static final String PERCENT_DISCOUNT_VOUCHER_TYPE = "percent";

    private VoucherRepository voucherRepository = new MemoryVoucherRepository();
    private VoucherService voucherService = new VoucherServiceImpl(voucherRepository);

    @DisplayName("옳바른 바우처 타입과 할인양 입력 시, createVoucher()를 실행하면 테스트가 성공한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "FIXED, 100",
            "PERCENT, 10"})
    void createVoucher(String voucherType, long discountAmount) {
        //given
        VoucherCreationRequest voucherCreationRequest = RequestFactory.createVoucherCreationRequest(voucherType, discountAmount);

        //when
        UUID voucherId = voucherService.createVoucher(voucherCreationRequest);

        //then
        assertThat(voucherRepository.findByVoucherId(voucherId)).isPresent();

    }

    @DisplayName("잘못된 바우처 타입 입력 시, createVoucher()를 실행하면 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "wrongType, 100",
            "weirdType, 10"})
    void inputIncorrectVoucherType(String voucherType, long discountAmount) {
        //given
        VoucherCreationRequest voucherCreationRequest = RequestFactory.createVoucherCreationRequest(voucherType, discountAmount);

        //when, then
        Assertions.assertThatThrownBy(() -> voucherService.createVoucher(voucherCreationRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("바우처 타입을 입력하지 않을 시, createVoucherCreationRequest()를 실행하면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void inputNullVoucherType(String voucherType) {
        //nothing given

        //when, then
        Assertions.assertThatThrownBy(() -> RequestFactory.createVoucherCreationRequest(voucherType, PERCENT_DISCOUNT_AMOUNT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("바우처 타입과 할인양을 입력해주세요.");
    }

    @DisplayName("바우처 할인양이 0일 경우 createVoucherCreationRequest()를 실행하면 예외가 발생한다.")
    @ParameterizedTest
    @EnumSource(VoucherType.class)
    void input0VoucherDiscountAmount(VoucherType voucherType) {
        //given
        final long discountAmount = 0;
        String requestVoucherType = voucherType.name().toLowerCase();

        //when, then
        Assertions.assertThatThrownBy(() -> RequestFactory.createVoucherCreationRequest(requestVoucherType, discountAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("바우처 타입과 할인양을 입력해주세요.");
    }

    @DisplayName("바우처 생성 및 저장 시, findVoucherList() 실행하면 바우처가 조회된다.")
    @Test
    void findVoucherList() {
        final int expectedCount = 2;
        //given
        VoucherCreationRequest voucherCreationRequest1 = RequestFactory.createVoucherCreationRequest(FIXED_AMOUNT_VOUCHER_TYPE, FIXED_DISCOUNT_AMOUNT);
        VoucherCreationRequest voucherCreationRequest2 = RequestFactory.createVoucherCreationRequest(PERCENT_DISCOUNT_VOUCHER_TYPE, PERCENT_DISCOUNT_AMOUNT);
        ArrayList<VoucherCreationRequest> voucherCreationRequests = new ArrayList<>(List.of(voucherCreationRequest1, voucherCreationRequest2));
        createAndSaveVoucher(voucherCreationRequests);

        //when
        List<VoucherInfoResponse> voucherList = voucherService.findVoucherList();

        //then
        assertThat(voucherList).hasSize(expectedCount);
    }

    private void createAndSaveVoucher(List<VoucherCreationRequest> voucherCreationRequestList) {
        voucherCreationRequestList
                .forEach(voucherService::createVoucher);
    }

    @DisplayName("바우처 생성 및 저장 시, 해당 바우처 아이디로 findVoucherByVoucherId() 실행하면 바우처가 조회된다.")
    @Test
    void findVoucherByVoucherId() {
        //given
        VoucherCreationRequest voucherCreationRequest = RequestFactory.createVoucherCreationRequest(FIXED_AMOUNT_VOUCHER_TYPE, FIXED_DISCOUNT_AMOUNT);
        UUID voucherId = voucherService.createVoucher(voucherCreationRequest);

        //when
        VoucherInfoResponse voucher = voucherService.findVoucherByVoucherId(voucherId);

        //then
        assertThat(voucher.voucherId()).isEqualTo(voucherId);
    }

    @DisplayName("존재하지 않는 바우처의 아이디로 findVoucherByVoucherId() 실행하면 예외가 발생한다.")
    @Test
    void throwExceptionWhenVoucherIdIsNonExist() {
        //given
        UUID nonExistVoucherId = UUID.randomUUID();

        //when, then
        assertThatThrownBy(() -> voucherService.findVoucherByVoucherId(nonExistVoucherId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage(String.format("해당 바우처 아이디로 조회되는 바우처가 없습니다. 입력값: %s", nonExistVoucherId));
    }
}
