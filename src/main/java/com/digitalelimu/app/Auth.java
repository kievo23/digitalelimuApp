package com.digitalelimu.app;

import com.digitalelimu.app.models.Content;
import com.digitalelimu.app.models.OAuth;
import com.digitalelimu.app.models.OAuthBook;
import com.digitalelimu.app.models.ReadBook;
import com.digitalelimu.app.models.Term;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by kev on 2/7/17.
 */

public interface Auth {
    @GET("{phone}/{password}")
    Call<OAuth> authUser(@Path("phone") String phone,@Path("password") String password);

    @POST("api/registerUser")
    Call<OAuth> authRegister(@Body OAuth reg);

    @POST("api/readBook")
    Call<ReadBook> authBook(@Body OAuthBook readBook);

    @GET("api/passwordreset/{phone}")
    Call<OAuth> passwordReset(@Path("phone") String phone);

    @GET("api/newpassword/{phone}/{password}/{code}")
    Call<Integer> newPassword(@Path("phone") String phone, @Path("password") String password,@Path("code") String code);

    @GET("api/getTerms/{phone}/{accesstoken}/{bookid}")
    Call<ArrayList<Term>> authTerm(@Path("phone") String phone,
                                  @Path("accesstoken") String accesstoken,
                                  @Path("bookid") String bookid);

    @GET("api/getWeeks/{phone}/{accesstoken}/{bookid}/{term}")
    Call<ArrayList<Term>> authWeek(@Path("phone") String phone,
                        @Path("accesstoken") String accesstoken,
                        @Path("bookid") String bookid,
                        @Path("term") String term);

    @GET("api/getLessons/{phone}/{accesstoken}/{bookid}/{term}/{week}")
    Call<ArrayList<Term>> authLesson(@Path("phone") String phone,
                            @Path("accesstoken") String accesstoken,
                            @Path("bookid") String bookid,
                            @Path("term") String term,
                            @Path("week") String week);

    @GET("api/getContent/{phone}/{accesstoken}/{bookid}/{term}/{week}/{lesson}")
    Call<Content> authContent(@Path("phone") String phone,
                              @Path("accesstoken") String accesstoken,
                              @Path("bookid") String bookid,
                              @Path("term") String term,
                              @Path("week") String week,
                              @Path("lesson") String lesson);
}