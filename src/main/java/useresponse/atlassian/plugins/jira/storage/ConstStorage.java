package useresponse.atlassian.plugins.jira.storage;

public class ConstStorage {

    public static final String  API_STRING = "api/4.0/";


//    public String getContent() {
//        ArrayList<Byte> content = new ArrayList<>();
//        InputStream input = null;
//
//        try {
//            input = context.getContentResolver().openInputStream(path);
//            byte[] buffer = new byte[1024];
//            int length;
//
//            if (input != null) {
//                while ((length = input.read(buffer)) > 0) {
//                    for (int i = 0; i < length; i++) {
//                        content.add(buffer[i]);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Log.e("UrLog", e.getMessage());
//        }
//
//        if (input != null) {
//            try {
//                input.close();
//            } catch (Exception e) {
//                Log.e("UrLog", e.getMessage());
//            }
//        }
//
//        byte[] contentArray = new byte[content.size()];
//
//        for (int i = 0; i < content.size(); i++) {
//            contentArray[i] = content.get(i);
//        }
//
//        return Base64.encodeToString(contentArray, 0);
//    }

}
