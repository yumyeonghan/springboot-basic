package com.programmers.voucher.enums;

import com.programmers.voucher.domain.FixedAmountVoucher;
import com.programmers.voucher.domain.PercentDiscountVoucher;
import com.programmers.voucher.domain.Voucher;
import com.programmers.voucher.exception.VoucherErrorCode;
import com.programmers.voucher.exception.VoucherException;
import com.programmers.voucher.request.VoucherCreationRequest;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

public enum VoucherType {
    FIXED_AMOUNT_VOUCHER("fixed", FixedAmountVoucher::new, 1000),
    PERCENT_DISCOUNT_VOUCHER("percent", PercentDiscountVoucher::new, 100);
    
    private final String voucherType;
    private final BiFunction<UUID, Long, Voucher> voucherCreationFunction;
    private final Integer maximumDiscountAmount;

    VoucherType(String voucherType, BiFunction<UUID, Long, Voucher> voucherCreationFunction, Integer maximumDiscountAmount) {
        this.voucherType = voucherType;
        this.voucherCreationFunction = voucherCreationFunction;
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    public static VoucherType getVoucherType(String type) {
        return Arrays.stream(VoucherType.values())
                .filter(voucherType -> Objects.equals(voucherType.voucherType, type))
                .findAny()
                .orElseThrow(() -> new VoucherException(VoucherErrorCode.NOT_SUPPORTED_TYPE));
    }

    public Voucher createVoucher(VoucherCreationRequest voucherCreationRequest) {
        validateOverDiscountAmount(voucherCreationRequest);
        return voucherCreationFunction.apply(UUID.randomUUID(), voucherCreationRequest.getAmount());
    }

    private static void validateOverDiscountAmount(VoucherCreationRequest voucherCreationRequest) {
        Arrays.stream(VoucherType.values())
                .filter(voucherType -> Objects.equals(voucherType.voucherType, voucherCreationRequest.getType())
                        && voucherType.maximumDiscountAmount > voucherCreationRequest.getAmount())
                .findAny()
                .orElseThrow(() -> new VoucherException(VoucherErrorCode.EXCEED_MAXIMUM_DISCOUNT_AMOUNT));
    }
}
