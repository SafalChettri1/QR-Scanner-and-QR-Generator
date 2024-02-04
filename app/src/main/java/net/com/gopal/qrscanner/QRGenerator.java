package net.com.gopal.qrscanner;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRGenerator extends AppCompatActivity {

    TextView CodeView;
    TextInputEditText editData;
    ImageView imageCode;
    Button generateCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        generateCode = findViewById(R.id.generateCode);
        editData = findViewById(R.id.editData);
        imageCode = findViewById(R.id.imageCode);
        CodeView = findViewById(R.id.CodeView);

        generateCode.setOnClickListener(view -> generateQRCode());

    }

    private void generateQRCode() {
        String data = editData.getText().toString().trim();
        if (!data.isEmpty()) {
            try {
                Bitmap bitmap = encodeAsBitmap(data);
                if (bitmap != null) {
                    imageCode.setImageBitmap(bitmap);
                    CodeView.setVisibility(View.INVISIBLE);
                    CodeView.setText("QR Successfully Generated");
                } else {
                    CodeView.setVisibility(View.VISIBLE);
                    CodeView.setText("Failed to generate QR code. Please try again.");
                }
            } catch (WriterException e) {
                e.printStackTrace();
                CodeView.setVisibility(View.VISIBLE);
                CodeView.setText("Failed to generate QR code. Please try again.");
            }
        } else {
            Toast.makeText(this, "Please enter data to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 500, 500);

        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[matrixWidth * matrixHeight];

        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                pixels[i * matrixWidth + j] = bitMatrix.get(j, i) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);

        return bitmap;
    }
}
