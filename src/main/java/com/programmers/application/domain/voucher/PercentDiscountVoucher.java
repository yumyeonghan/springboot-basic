package com.programmers.application.domain.voucher;

import java.util.Objects;
import java.util.UUID;

public class PercentDiscountVoucher extends Voucher{
    private static final int MAX_DISCOUNT_AMOUNT = 100;
    private static final int MIN_DISCOUNT_AMOUNT = 0;

    private final long discountAmount;

    private PercentDiscountVoucher(UUID voucherId, long discountAmount) {
        super(voucherId);
        this.discountAmount = discountAmount;
    }

    public static Voucher of(UUID voucherId, long discountAmount) {
        validateVoucherId(voucherId);
        validateDiscountAmount(discountAmount);
        return new PercentDiscountVoucher(voucherId, discountAmount);
    }

    @Override
    public long discount(long originalPrice) {
        return originalPrice - (originalPrice * discountAmount / MAX_DISCOUNT_AMOUNT);
    }

    @Override
    public long getDiscountAmount() {
        return discountAmount;
    }

    @Override
    public VoucherType getVoucherType() {
        return VoucherType.PERCENT;
    }

    private static void validateVoucherId(UUID voucherId) {
        if (Objects.isNull(voucherId)) {
            throw new IllegalArgumentException("바우처 아이디가 비어있습니다.");
        }
    }

    private static void validateDiscountAmount(long discountAmount) {
        if (discountAmount > MAX_DISCOUNT_AMOUNT || discountAmount < MIN_DISCOUNT_AMOUNT) {
            String errorMessage = String.format("%d ~ %d 범위의 바우처 할인양을 입력해주세요. 입력값: %d", MIN_DISCOUNT_AMOUNT, MAX_DISCOUNT_AMOUNT, discountAmount);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
