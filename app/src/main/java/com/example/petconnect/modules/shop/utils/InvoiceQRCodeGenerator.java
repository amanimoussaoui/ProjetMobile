package com.example.petconnect.modules.shop.utils;

import android.content.Context;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class InvoiceQRCodeGenerator {
    private static final int QR_CODE_WIDTH = 512;
    private static final int QR_CODE_HEIGHT = 512;

    public static Bitmap generateInvoiceQRCode(String invoiceNumber, double totalAmount) {
        String qrData = String.format("INVOICE|%s|%.2f", invoiceNumber, totalAmount);
        return generateQRCode(qrData);
    }

    public static Bitmap generateQRCode(String text) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateInvoiceShareLink(String invoiceNumber) {
        return "https://petconnect.app/invoice/" + invoiceNumber;
    }
}
