package troop.com.imageviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.troop.androiddng.DngSupportedDevices;
import com.troop.androiddng.Matrixes;
import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;

/**
 * Created by troop on 22.12.2015.
 */
public class DngConvertingFragment extends Fragment
{
    View view;
    EditText editTextwidth;
    EditText editTextheight;
    Spinner spinnerMatrixProfile;
    Spinner spinnerColorPattern;
    Button buttonconvertToDng;
    String[] filesToConvert;
    DngSupportedDevices.DngProfile dngprofile;

    public static final String EXTRA_FILESTOCONVERT = "extra_files_to_convert";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.contex = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.dngconvertingfragment, container, false);
        this.editTextwidth = (EditText)view.findViewById(R.id.editText_width);
        this.editTextheight = (EditText)view.findViewById(R.id.editText_height);
        this.spinnerMatrixProfile = (Spinner)view.findViewById(R.id.spinner_MatrixProfile);
        ArrayAdapter<CharSequence> matrixadapter = ArrayAdapter.createFromResource(getContext(),
                R.array.matrixes, android.R.layout.simple_spinner_item);
        matrixadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatrixProfile.setAdapter(matrixadapter);
        spinnerMatrixProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        dngprofile.matrix1 = Matrixes.Nex6CCM1;
                        dngprofile.matrix2 = Matrixes.Nex6CCM2;
                        dngprofile.neutral = Matrixes.Nex6NM;
                        dngprofile.fowardmatrix1 = Matrixes.Nexus6_foward_matrix1;
                        dngprofile.fowardmatrix2 = Matrixes.Nexus6_foward_matrix2;
                        dngprofile.reductionmatrix1= Matrixes.Nexus6_reduction_matrix1;
                        dngprofile.reductionmatrix1= Matrixes.Nexus6_reduction_matrix2;
                        dngprofile.noiseprofile = Matrixes.Nexus6_noise_3x1_matrix;
                        break;
                    case 1:
                        dngprofile.matrix1 = Matrixes.G4CCM1;
                        dngprofile.matrix2 = Matrixes.G4CCM2;
                        dngprofile.neutral = Matrixes.G4NM;
                        dngprofile.fowardmatrix1 = Matrixes.G4_foward_matrix1;
                        dngprofile.fowardmatrix2 = Matrixes.G4_foward_matrix2;
                        dngprofile.reductionmatrix1= Matrixes.G4_reduction_matrix1;
                        dngprofile.reductionmatrix1= Matrixes.G4_reduction_matrix2;
                        dngprofile.noiseprofile = Matrixes.G4_noise_3x1_matrix;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.buttonconvertToDng = (Button)view.findViewById(R.id.button_convertDng);
        buttonconvertToDng.setOnClickListener(convertToDngClick);

        this.spinnerColorPattern =(Spinner)view.findViewById(R.id.spinner_ColorPattern);
        ArrayAdapter<CharSequence> coloradapter = ArrayAdapter.createFromResource(getContext(),
                R.array.color_pattern, android.R.layout.simple_spinner_item);
        coloradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColorPattern.setAdapter(coloradapter);
        spinnerColorPattern.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        dngprofile.BayerPattern = DngSupportedDevices.BGGR;
                        break;
                    case 1:
                        dngprofile.BayerPattern = DngSupportedDevices.RGGB;
                        break;
                    case 2:
                        dngprofile.BayerPattern = DngSupportedDevices.GRBG;
                        break;
                    case 3:
                        dngprofile.BayerPattern = DngSupportedDevices.GBRG;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.filesToConvert = getActivity().getIntent().getStringArrayExtra(EXTRA_FILESTOCONVERT);
        if (filesToConvert != null && filesToConvert.length > 0)
        {
            DngSupportedDevices.SupportedDevices devices = DngSupportedDevices.getDevice();
            dngprofile = new DngSupportedDevices().getProfile(devices,(int) new File(filesToConvert[0]).length());
            if(dngprofile == null)
            {
                dngprofile = new DngSupportedDevices().GetEmptyProfile();
                Toast.makeText(getContext(),"Unknown RawFile, pls add needed Stuff Manual", Toast.LENGTH_LONG);
            }
            editTextwidth.setText(dngprofile.widht + "");
            editTextheight.setText(dngprofile.height + "");
            if (dngprofile.BayerPattern.equals(DngSupportedDevices.BGGR))
                spinnerColorPattern.setSelection(0);
            else if (dngprofile.BayerPattern.equals(DngSupportedDevices.RGGB))
                spinnerColorPattern.setSelection(1);
            else if (dngprofile.BayerPattern.equals(DngSupportedDevices.GRBG))
                spinnerColorPattern.setSelection(2);
            else if (dngprofile.BayerPattern.equals(DngSupportedDevices.GBRG))
                spinnerColorPattern.setSelection(0);
            if (dngprofile.matrix1.equals(Matrixes.Nex6CCM1))
                spinnerMatrixProfile.setSelection(0);
            else
                spinnerMatrixProfile.setSelection(1);
        }
    }

    private View.OnClickListener convertToDngClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {

        }
    };


}