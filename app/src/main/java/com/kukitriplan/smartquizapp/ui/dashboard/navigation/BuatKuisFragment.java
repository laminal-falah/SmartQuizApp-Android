package com.kukitriplan.smartquizapp.ui.dashboard.navigation;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class BuatKuisFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    @BindView(R.id.spinnerKategori) Spinner spKategori;
    @BindView(R.id.spinnerMapel) Spinner spMapel;
    @BindView(R.id.spinnerHrg) Spinner spHarga;

    private String[] kategori = new String[] {
            "Pilih Kategori",
            "SD Kelas 1"
    };
    private String[] mapel = new String[] {
            "Pilih Mata Pelajaran",
            "Matematika"
    };
    private String[] harga = new String[] {
            "Pilih Harga Kuis",
            "0","2500","5000","10000","20000","25000","50000","100000"
    };

    private List<String> kategoriList = new ArrayList<>(Arrays.asList(kategori));
    private List<String> mapelList = new ArrayList<>(Arrays.asList(mapel));
    private List<String> hargaList = new ArrayList<>(Arrays.asList(harga));

    @BindView(R.id.cover) ImageView coverKuis;
    @BindView(R.id.btnPilihGambar) Button btnPilihGambar;
    private Bitmap bitmap, decoded;

    private OnFragmentInteractionListener mListener;

    public BuatKuisFragment() {
        // Required empty public constructor
    }

    public static BuatKuisFragment newInstance(String param1, String param2) {
        BuatKuisFragment fragment = new BuatKuisFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buat_kuis, container, false);
        ButterKnife.bind(this,view);
        btnPilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilGallery();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getKategori();
        getMapel();
        getHarga();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && requestCode == RESULT_OK && data != null && data.getData() != null) {

        }
        Uri filePath = data.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
            setToImageView(getResizedBitmap(bitmap, 300));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tampilGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
    }

    private void kosong() {
        coverKuis.setImageResource(0);
    }

    private void setToImageView(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        coverKuis.setImageBitmap(decoded);
    }

    private Bitmap getResizedBitmap(Bitmap img, int maxSize) {
        int width = img.getWidth();
        int height = img.getHeight();
        float bitmapRation = (float) width / (float) height;
        if (bitmapRation > 1) {
            width = maxSize;
            height = (int) (width / bitmapRation);
        } else {
            height = maxSize;
            width = (int) (height / bitmapRation);
        }
        return Bitmap.createScaledBitmap(img, width, height, true);
    }
    private void getKategori() {
        ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, kategoriList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        kategoriAdapter.setDropDownViewResource(R.layout.spinner_item);
        spKategori.setAdapter(kategoriAdapter);
        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getMapel() {
        ArrayAdapter<String> mapelAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, mapelList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        mapelAdapter.setDropDownViewResource(R.layout.spinner_item);
        spMapel.setAdapter(mapelAdapter);
        spMapel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getHarga() {
        ArrayAdapter<String> hargaAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, hargaList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        hargaAdapter.setDropDownViewResource(R.layout.spinner_item);
        spHarga.setAdapter(hargaAdapter);
        spHarga.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
