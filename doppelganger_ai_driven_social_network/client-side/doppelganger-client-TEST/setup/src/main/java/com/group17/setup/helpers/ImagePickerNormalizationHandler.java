package com.group17.setup.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ImagePickerNormalizationHandler extends DefaultCallback {

    protected Context context = null;
    protected byte[] outputImage = null;

    public ImagePickerNormalizationHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
        byte[] inputImage = null;

        try {
            inputImage = Files.toByteArray(imageFile);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(inputImage, 0, inputImage.length);

            // Crop the image to be a square
            if(bitmapImage.getHeight() > bitmapImage.getWidth()) {
                bitmapImage = Bitmap.createBitmap(
                    bitmapImage,
                    0,
                    (bitmapImage.getHeight() - bitmapImage.getWidth()) / 2,
                    bitmapImage.getWidth(),
                    bitmapImage.getWidth()
                );
            } else {
                bitmapImage = Bitmap.createBitmap(
                    bitmapImage,
                    (bitmapImage.getWidth() - bitmapImage.getHeight()) / 2,
                    0,
                    bitmapImage.getHeight(),
                    bitmapImage.getHeight()
                );
            }

            // Resize the image to be 400x400
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, 400, 400, true);

            // Prepare the picture for sending
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            outputImage = byteStream.toByteArray();
        } catch (IOException e) {
            // TODO: Kakvo moze da stane tuka??? permissions???
            e.printStackTrace();
        }
    }

    // TODO: Koga ima error???
    // NOTE: Daden e exception object kato argument!!!
    @Override
    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
        Toast.makeText(context, "Error Handling", Toast.LENGTH_SHORT).show();
    }
}