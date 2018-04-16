package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserSystem {
    private GoogleSignInAccount account;
    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private PeopleService peopleService;
    private List<List<String>> contacts_data;
    private List<String> profile_data;
    private HttpTransport httpTransport = new NetHttpTransport();
    private JacksonFactory jsonFactory = new JacksonFactory();
    private Activity root;
    private UserState state;
    private boolean isReady = false;

    public UserSystem(Activity root, UserState state, String CLIENT_ID, String CLIENT_SECRET) {
        this.root = root;
        this.state = state;
        this.CLIENT_ID = CLIENT_ID;
        this.CLIENT_SECRET = CLIENT_SECRET;
        account = GoogleSignIn.getLastSignedInAccount(root);
        new ProfileBuilderTask().execute(account);
    }

    public class ProfileBuilderTask extends AsyncTask<GoogleSignInAccount, Integer, String> {
        @Override
        protected String doInBackground(GoogleSignInAccount... account) {
            try {
                GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory,
                        CLIENT_ID, CLIENT_SECRET, account[0].getServerAuthCode(), "").execute();
                GoogleCredential credential = new GoogleCredential.Builder()
                        .setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                        .build()
                        .setFromTokenResponse(tokenResponse);
                peopleService = new PeopleService.Builder(httpTransport, jsonFactory, credential).build();
                Person profile = peopleService.people().get("people/me")
                        .setPersonFields("names,emailAddresses")
                        .execute();
                String res_name = profile.getResourceName();
                String name = profile.getNames().get(0).getDisplayName();
                String email = profile.getEmailAddresses().get(0).getValue();
                profile_data = new ArrayList<>();
                profile_data.add(res_name);
                profile_data.add(name);
                profile_data.add(email);
                contacts_data = buildConnectionsHandler(peopleService);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            UserDataStorage.setProfile(profile_data);
            UserDataStorage.setContacts(contacts_data);
            state.setUser(UserDataStorage.getUID());
        }

    }

    private List<List<String>> buildConnectionsHandler(PeopleService peopleService) {
        List<List<String>> contacts = null;
        try {
            contacts = buildConnections(peopleService);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private List<List<String>> buildConnections(PeopleService peopleService) throws IOException {
        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
                .setPersonFields("names")
                .execute();
        List<Person> connections = response.getConnections();
        List<List<String>> contacts = new ArrayList<>();
        if (response.size() > 0) {
            for (Person person : connections) {
                if (!person.isEmpty()) {
                    List<Name> namesList = person.getNames();
                    String name;
                    try {
                        name = namesList.get(0).getDisplayName();
                        // TODO changing stuff here
                    }
                    catch(NullPointerException e) {
                        name = "No name found";
                    }
                    String resName = person.getResourceName();

                    List<String> contact = new ArrayList<>();
                    contact.add(resName);
                    contact.add(name);
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }

}
