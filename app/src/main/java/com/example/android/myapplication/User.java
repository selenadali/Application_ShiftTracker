package com.example.android.myapplication;

        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.util.Log;
        import com.example.android.myapplication.User;

        import android.support.annotation.NonNull;
        import android.util.Log;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;

        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.EventListener;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.FirebaseFirestoreException;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;
        import com.google.firebase.firestore.Source;


public class User {
    String Name;
    String Mail;
    Integer Type; //Groupes(1) or Admins(0)
    String Titre;
    String tel;
    String photo;
    String adresse;
    String salaire;
    String contrat;
    String datecommence;
    //If Emp or Resp
    Integer TypeEmp; // Resp(1) or Emp(0)
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    public static User createUser_Emp_Resp(String name, String mail, String titre, Integer typeEmp, Integer type, String tel, String adresse, String datecommence, String salaire, String contrat, String photo){
        User user = new User();
        user.Name = name;
        user.Mail = mail;
        user.tel = tel;
        user.photo = photo;
        user.adresse = adresse;
        user.salaire = salaire;
        user.contrat = contrat;
        user.datecommence = datecommence;
        user.Type = type;
        user.TypeEmp = typeEmp; // 1 if Resp
        return user;
    }
//  UserInfo = userName,userMail,1,typeEmpResp,userTitre,adresse,tel,userSalaire,userContrat,userDateCommence
public static void AddUserToDb(FirebaseFirestore db, String[] userInfo) {
        for(String u : userInfo) {
            String[] UInfo = u.split(",");
            User user = new User();
            user.Name = UInfo[0];
            user.Mail = UInfo[1];
            user.Type = Integer.parseInt(UInfo[2]);
            user.TypeEmp =Integer.parseInt(UInfo[3]);
            user.Titre = UInfo[4];
            user.adresse = UInfo[5];
            user.tel = UInfo[6];
            user.salaire = UInfo[7];
            user.contrat = UInfo[8];
            user.datecommence = UInfo[9];
            user.photo = UInfo[10];

            String collectionPath = null;
            if (user.Type == 1) {
                collectionPath = "/Users/" ;
            } else if (user.Type == 0){
                user.TypeEmp = null;
                collectionPath = "/Admins/";
            }
            db.collection(collectionPath).document(user.Mail).set(user);
        }
    }
}