package com.programmers.voucher.service;

import com.programmers.voucher.repository.MemoryVoucherRepository;
import com.programmers.voucher.repository.VoucherRepository;
import com.programmers.voucher.request.VoucherCreationRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VoucherServiceTest {
    public static final int FIXED_DISCOUNT_AMOUNT = 100;
    public static final int PERCENT_DISCOUNT_AMOUNT = 10;
    public static final String FIXED_AMOUNT_VOUCHER_TYPE = "fixed";
    public static final String PERCENT_DISCOUNT_VOUCHER_TYPE = "percent";
    public static final String WRONG_VOUCHER_TYPE = "wrongType";
    public static final int NEGATIVE_NUMBER = -1;
    public static final int OVER_FIXED_AMOUNT = 1001;
    public static final int OVER_PERCENT_AMOUNT = 101;

    private VoucherRepository voucherRepository;
    private VoucherService voucherService;

    @BeforeEach
    void 초기화() {
        voucherRepository = new MemoryVoucherRepository();
        voucherRepository.clear();
        voucherService = new VoucherService(voucherRepository);
    }

    @Test
    void createFixedAmountVoucher() {
        VoucherCreationRequest voucherCreationRequest = new VoucherCreationRequest(FIXED_AMOUNT_VOUCHER_TYPE, FIXED_DISCOUNT_AMOUNT);
        voucherService.createVoucher(voucherCreationRequest);
    }

    @Test
    void createPercentDiscountVoucher() {
        VoucherCreationRequest voucherCreationRequest = new VoucherCreationRequest(PERCENT_DISCOUNT_VOUCHER_TYPE, PERCENT_DISCOUNT_AMOUNT);
        voucherService.createVoucher(voucherCreationRequest);
    }

    @Test
    void 잘못된_바우처_타입_입력시_예외_발생() {
        VoucherCreationRequest voucherCreationRequest = new VoucherCreationRequest(WRONG_VOUCHER_TYPE, PERCENT_DISCOUNT_AMOUNT);
        Assertions.assertThatThrownBy(() -> voucherService.createVoucher(voucherCreationRequest))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("지원하지 않는 바우처입니다.");
    }

    @Test
    void 셍성할_바우처_타입이_null_이면_예외_발생() {
        Assertions.assertThatThrownBy(() -> new VoucherCreationRequest(null, PERCENT_DISCOUNT_AMOUNT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 바우처 생성 요청입니다.");
    }

    @Test
    void 생성할_바우처_할인_양이_음수면_예외_발생() {
        Assertions.assertThatThrownBy(() -> new VoucherCreationRequest(FIXED_AMOUNT_VOUCHER_TYPE, NEGATIVE_NUMBER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 바우처 생성 요청입니다.");
    }

    @Test
    void 생성할_FixedAmountVoucher_할인_양이_초과면_예외_발생() {
        VoucherCreationRequest voucherCreationRequest = new VoucherCreationRequest(FIXED_AMOUNT_VOUCHER_TYPE, OVER_FIXED_AMOUNT);
        Assertions.assertThatThrownBy(() -> voucherService.createVoucher(voucherCreationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("바우처의 최대 할인 양을 초과하였습니다.");
    }

    @Test
    void 생성할_PercentDiscountVoucher_할인_양이_초과면_예외_발생() {
        VoucherCreationRequest voucherCreationRequest = new VoucherCreationRequest(PERCENT_DISCOUNT_VOUCHER_TYPE, OVER_PERCENT_AMOUNT);
        Assertions.assertThatThrownBy(() -> voucherService.createVoucher(voucherCreationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("바우처의 최대 할인 양을 초과하였습니다.");
    }

    @Test
    void findVoucherList() {
        VoucherCreationRequest voucherCreationRequest1 = new VoucherCreationRequest(FIXED_AMOUNT_VOUCHER_TYPE, FIXED_DISCOUNT_AMOUNT);
        VoucherCreationRequest voucherCreationRequest2 = new VoucherCreationRequest(PERCENT_DISCOUNT_VOUCHER_TYPE, PERCENT_DISCOUNT_AMOUNT);
        voucherService.createVoucher(voucherCreationRequest1);
        voucherService.createVoucher(voucherCreationRequest2);
        Assertions.assertThat(voucherService.findVoucherList().size()).isEqualTo(2);
    }
    
}