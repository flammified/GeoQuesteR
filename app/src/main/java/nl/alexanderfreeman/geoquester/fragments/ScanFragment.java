package nl.alexanderfreeman.geoquester.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import nl.alexanderfreeman.geoquester.MainScreenActivity;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.utility.Utility;

import static android.app.Activity.RESULT_OK;
import static nl.alexanderfreeman.geoquester.R.string.scan;

/**
 * Created by Alexander Freeman on 20-6-2017.
 */

public class ScanFragment extends Fragment {

    private static final int PHOTO_REQUEST = 1;
    private BarcodeDetector detector;
    private Uri imageUri;

    private ImageView image;
    private TextView status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.scan_fragment, container, false);
        Log.d("DEBUG", "I am here");
        detector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.QR_CODE).build();

        if (!detector.isOperational()) {
            Toast.makeText(getContext(), "Could not set up the detector", Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "Not available");
            return root;
        }

        image = (ImageView) root.findViewById(R.id.qr_image);
        status = (TextView) root.findViewById(R.id.status_text);

        Button scan = (Button) root.findViewById(R.id.scan);

        Log.d("DEBUG", "I am here");
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status.setText("");
                takePicture();
            }
        });

        return root;
    };

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        status.setText("");
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(getContext(), imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    image.setImageBitmap(bitmap);
                    SparseArray<Barcode> barcodes = detector.detect(frame);

                    if (barcodes.size() == 0) {
                        status.setText("This is not a QR code. Please scan a QR code.");
                        return;
                    }

                    Log.d("DEBUG", "" + barcodes.size());

                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        Toast.makeText(getContext(), code.displayValue, Toast.LENGTH_LONG).show();
                        validate_qr_code(code.displayValue);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Failed to load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePicture() {
        Log.d("DEBUG", "TakePicture");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    private void onFireBaseQRResult(GeoQuest quest, String id) {
        status.setText("Congrats ;)");
        ((MainScreenActivity) getActivity()).switch_to_congrats(quest, id);
    }

    private void validate_qr_code(final String id) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference questRef = FirebaseDatabase.getInstance().getReference("quests");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + user);

        questRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    userRef.child("found/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (snapshot.exists()) {

                                final GeoQuest quest = snapshot.getValue(GeoQuest.class);
                                SmartLocation.with(getActivity()).location()
                                        .oneFix()
                                        .start(new OnLocationUpdatedListener() {

                                            @Override
                                            public void onLocationUpdated(Location location) {

                                                if (location.isFromMockProvider()) {
                                                    Utility.fakeLocationDialogAndQuit(getActivity());
                                                }

                                                Location l = new Location("quest");
                                                l.setLatitude(quest.getLatitude());
                                                l.setLongitude(quest.getLongitude());

                                                if (location.distanceTo(l) < 50) {
                                                    onFireBaseQRResult(quest, snapshot.getKey());
                                                } else {
                                                    status.setText("You are too far away.");
                                                }
                                            }

                                        });
                            } else {
                                status.setText("Already found! No more points for you ;)");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            status.setText("Something went wrong; try again");
                        }
                    });
                }
                else {
                    status.setText("Quest does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                status.setText("Something went wrong; try again");
            }
        });
    }
}