
public class upload extends Fragment {

    
    static Activity activity;
    
    ImageView cam;
    
    int PICK_IMAGE_MULTIPLE = 1;
    int PICK_VIDEO =0;
    int PICK_VIDEO_CAMERA = 300;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_GALLERY_CODE= 200;
    private Uri uri;
    ArrayList<Uri> imageuploadlist=new ArrayList<>();
    List<MultipleImageData> imagedata;
    
    List<String> imageslist = null;
   
    
    ArrayList<String> selectedimagelist = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment,container,false);
       
    
        cam=v.findViewById(R.id.cam);
       
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = { "Capture photo from Camera","Capture video from Camera","Choose photo from Gallery","Choose video from Gallery","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle("Select Image");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Capture from Camera"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                            startActivityForResult(intent, REQUEST_CAMERA_CODE);
                        }
                        if (options[item].equals("Choose photo from Gallery")){

                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/* , video/*");
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});

//                            intent.setType("video/* , image/* ");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(intent, PICK_IMAGE_MULTIPLE);


//                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                            photoPickerIntent.setType("*/*");
//                            photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
//                            startActivityForResult(intent, PICK_IMAGE_MULTIPLE);

                        }

                        if(options[item].equals("Choose video from Gallery")){

                            Intent videointent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            videointent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(videointent, PICK_VIDEO);
                        }

                        if(options[item].equals("Capture video from Camera")){
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.mp4");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                            startActivityForResult(intent,PICK_VIDEO_CAMERA);
                        }

                        else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });

                builder.show();
            }
        });

       
        return v;
    }


   
    private ArrayList<String> getallselectedimagespath(ClipData data) {
        int count = data.getItemCount();
        Log.d("mytag","Selected Images Count :: " + count);
        for (int i = 0; i < count; i++) {
            Uri imageUri = data.getItemAt(i).getUri();
            String filePath = getRealPathFromURIPath(imageUri, activity);
            Log.d("mytag", "Selected Images :: " + count + " Uri - " + i + " ::" + data.getItemAt(i).getUri() + " FilePath - " + i + " ::" + filePath);
            selectedimagelist.add(filePath);
        }
        return selectedimagelist;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                String filePath = getRealPathFromURIPath(resultUri, activity);
                selectedimagelist.add(filePath);
                multipleImageUpload(selectedimagelist);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CODE) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        Log.d("mytag", "f=temp");
                        break;
                    }
                }

                CropImage.activity(Uri.fromFile(f)).start(getContext(), this);

            } else if (requestCode == PICK_IMAGE_MULTIPLE) {
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getClipData() != null) {
                        selectedimagelist = getallselectedimagespath(data.getClipData());
                        multipleImageUpload(selectedimagelist);
                    } else if (data.getData() != null) {
                        Uri imagePath = data.getData();
                        String filePath = getRealPathFromURIPath(imagePath, activity);
                        selectedimagelist.add(filePath);
                        multipleImageUpload(selectedimagelist);
//                        imageuploadlist = new ArrayList<>();
//                        int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
//                        for (int i = 0; i < count; i++) {
//                            Log.d("mytag", "Selected Images url:--" + data.getClipData().getItemAt(i).getUri());
//                            Uri imageUri1 = data.getClipData().getItemAt(i).getUri();
//                            imageuploadlist.add(imageUri1);
////                            RecyclerView.Adapter adapter = new PostPicsAdapter(activity, fragmentManager, toolbar, mDrawerLayout, imagesEncodedList);
////                            img_recycler.setAdapter(adapter);
//                            multipleImageUpload(imageuploadlist);
//                        }
//
//                    } else if (data.getData() != null) {
////                        imageuploadlist = new ArrayList<>();
//                        Uri imagePath = data.getData();
//                        imageuploadlist.add(imagePath);
//                        Toast.makeText(activity, "data", Toast.LENGTH_SHORT).show();
////                        RecyclerView.Adapter adapter = new PostPicsAdapter(activity, fragmentManager, toolbar, mDrawerLayout, imagesEncodedList);
////                        img_recycler.setAdapter(adapter);
//                        multipleImageUpload(imageuploadlist);

                    }
                }
            }else if(requestCode==PICK_VIDEO){
                Log.d("mytag","in video" +data.getData());

                uri = data.getData();

                String filePath = getRealPathFromURIPath(uri, activity);
                File file = new File(filePath);
                uploadvideotoserver(file); //video upload
            }

            else if(requestCode==PICK_VIDEO_CAMERA){

                uri = data.getData();
                Log.d("mytag","in video" +data.getData());
                String filePath = getRealPathFromURIPath(uri, activity);
                File file = new File(filePath);
                uploadvideotoserver(file);
            }
        }
    }

    private void uploadvideotoserver(File file) {

        RequestBody mFile = RequestBody.create(MediaType.parse("video/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ImageUploadResponse> fileUpload = apiInterface.UploadImage(Prefs.getPrefInstance().getValue(activity,Const.ACCESS_TOKEN,""), fileToUpload);
        fileUpload.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    int status = response.body().getStatus();
                    if (status == 1) {

                        videostring = response.body().getData();
                        attemptSend("",3);

                    }
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {

            }
        });
    }


    private void Callapi(MultipartBody.Part[] img) {
        Call<MultipleImageResponse> multipleImageResponseCall = APIClient.getClient().create(APIInterface.class).UploadMultipleImage(Prefs.getPrefInstance().getValue(activity, Const.ACCESS_TOKEN, ""), img);
        multipleImageResponseCall.enqueue(new Callback<MultipleImageResponse>() {
            @Override
            public void onResponse(Call<MultipleImageResponse> call, Response<MultipleImageResponse> response) {
                if (response.isSuccessful()) {
                    int status = response.body().getStatus();
                    if (status == 1) {
                        imagedata = response.body().getData();
                        attemptSend("",2);
                        Log.d("mytag", "size is : " + imagedata.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<MultipleImageResponse> call, Throwable t) {

            }
        });
    }

    private MultipartBody.Part[] multipleImageUpload(ArrayList<String> selectedimagelist) {
        MultipartBody.Part[] imagepart = new MultipartBody.Part[selectedimagelist.size()];
        for (int index = 0; index < selectedimagelist.size(); index++) {
            File file = new File(selectedimagelist.get(index));
            RequestBody mfile = RequestBody.create(MediaType.parse("image/*"), file);
            imagepart[index] = MultipartBody.Part.createFormData("image[]", file.getName(), mfile);
        }
        Callapi(imagepart);
        selectedimagelist.clear();
        return imagepart;
    }

//    private MultipartBody.Part[] multipleImageUpload(ArrayList<Uri> imagesEncodedList) {
//        MultipartBody.Part[] imagepart = new MultipartBody.Part[imagesEncodedList.size()];
//        int count = 0;
//        for (int index = 0; index < imagesEncodedList.size(); index++) {
//            Log.d("mytag", "requestUploadSurvey: survey image " + index + "  " + imagesEncodedList.get(index));
//            String filePath = getRealPathFromURIPath(imagesEncodedList.get(index), activity);
//            File file = new File(filePath);
//            count++;
//            RequestBody mfile = RequestBody.create(MediaType.parse("image/*"), file);
//            imagepart[index] = MultipartBody.Part.createFormData("image[]", file.getName(), mfile);
//        }
//
//        Log.d("mytag","upload size is : "+imagesEncodedList.size());
//        imageUrl=new ArrayList<>();
//        Callapi(imagepart);
//        imageuploadlist.clear();
//        return imagepart;
//    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       
    }


    
}

