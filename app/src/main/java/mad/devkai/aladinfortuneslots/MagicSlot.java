package mad.devkai.aladinfortuneslots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Objects;

import mad.devkai.aladinfortuneslots.help.GlobalWebView;

public class MagicSlot extends AppCompatActivity {
    private static final int COMBO = 7;
    private static final int COEF = 72;
    private static final int COEFW = 142;
    private static final int COEFE = 212;
    private int position1 = 5;
    private int position2 = 5;
    private int position3 = 5;
    private final int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView recv1;
    private RecyclerView recv2;
    private RecyclerView recv3;
    private CustomManager layoutManager1;
    private CustomManager layoutManager2;
    private CustomManager layoutManager3;


    private TextView energyBallPrice;
    private TextView myPower;
    private TextView myPlay;

    int myCoinsval;
    int playval;
    int fortuneVal;

    private boolean firstRun;
    private boolean isSpinning = false;

    private Mechanics gameLogic;

    private SharedPreferences pref;
    public MediaPlayer musicPlay;
    public MediaPlayer win;

    public MediaPlayer plumin;
    public MediaPlayer bgsound;
    public static final String PREFS_NAME = "FirstRun";


    private int playmusic;
    private int playsound;
    private ImageView musicOff;
    private ImageView musicOn;
    private ImageView soundon;
    private ImageView soundoff;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_slot);


        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animation);
        videoView.setVideoURI(videoUri);
        videoView.start();

        ImageButton minusButton;
        ImageButton plusButton;
        SpinnerAdapter adapter;
        ImageView settingsButton;
        ImageButton spinButton;



        bgsound = MediaPlayer.create(this, R.raw.background_music);
        bgsound.setLooping(true);
        musicPlay = MediaPlayer.create(this, R.raw.dragon_spin);
        win = MediaPlayer.create(this, R.raw.won);
        plumin = MediaPlayer.create(this, R.raw.button);

        pref = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firstRun = pref.getBoolean("firstRun", true);


        if (firstRun) {
            playmusic = 1;
            playsound = 1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            playmusic = pref.getInt("music", 1);
            playsound = pref.getInt("sound", 1);
            checkmusic();

        }

        Log.d("MUSIC", String.valueOf(playmusic));

        //Initializations

        gameLogic = new Mechanics();
        settingsButton = findViewById(R.id.settings);
        spinButton = findViewById(R.id.spinButton);
        plusButton = findViewById(R.id.plusButton);
        minusButton = findViewById(R.id.minusButton);
        energyBallPrice = findViewById(R.id.fortuneprice);
        myPower = findViewById(R.id.energy);
        myPlay = findViewById(R.id.bet);
        adapter = new SpinnerAdapter(this, slot, gameLogic);

        //RecyclerView settings
        recv1 = findViewById(R.id.spinner1);
        recv2 = findViewById(R.id.spinner2);
        recv3 = findViewById(R.id.spinner3);
        recv1.setHasFixedSize(true);
        recv2.setHasFixedSize(true);
        recv3.setHasFixedSize(true);

        layoutManager1 = new CustomManager(this);
        layoutManager1.setScrollEnabled(false);
        recv1.setLayoutManager(layoutManager1);
        layoutManager2 = new CustomManager(this);
        layoutManager2.setScrollEnabled(false);
        recv2.setLayoutManager(layoutManager2);
        layoutManager3 = new CustomManager(this);
        layoutManager3.setScrollEnabled(false);
        recv3.setLayoutManager(layoutManager3);

        recv1.setAdapter(adapter);
        recv2.setAdapter(adapter);
        recv3.setAdapter(adapter);
        recv1.scrollToPosition(position1);
        recv2.scrollToPosition(position2);
        recv3.scrollToPosition(position3);

        setText();
        updateText();


        //RecyclerView listeners
        recv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recv1.scrollToPosition(gameLogic.getPosition(0));
                    layoutManager1.setScrollEnabled(false);
                }
            }
        });

        recv2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recv2.scrollToPosition(gameLogic.getPosition(1));
                    layoutManager2.setScrollEnabled(false);
                }
            }
        });
        recv3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recv3.scrollToPosition(gameLogic.getPosition(2));
                    layoutManager3.setScrollEnabled(false);
                    updateText();
                    if (gameLogic.getHasWon()) {
                        if (playsound == 1) {
                            win.start();
                        }
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.win_message, findViewById(R.id.win_splash));
                        TextView winCoins = layout.findViewById(R.id.win_coins);
                        winCoins.setText(gameLogic.getPrize());
                        Toast toast = new Toast(MagicSlot.this);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setView(layout);
                        toast.show();
                        gameLogic.setHasWon(false);

                    }
                    isSpinning = false;
                    spinButton.setEnabled(true);
                }

            }
        });

        spinButton.setOnClickListener(v -> {
            spinButton.setEnabled(false);
            if (!isSpinning) {
                if (playsound == 1) {
                    musicPlay.start();
                }
                isSpinning = true;
            }
            layoutManager1.setScrollEnabled(true);
            layoutManager2.setScrollEnabled(true);
            layoutManager3.setScrollEnabled(true);
            gameLogic.getSpinResults();
            position1 = gameLogic.getPosition(0) + COEF;
            position2 = gameLogic.getPosition(1) + COEFW;
            position3 = gameLogic.getPosition(2) + COEFE;
            recv1.smoothScrollToPosition(position1);
            recv2.smoothScrollToPosition(position2);
            recv3.smoothScrollToPosition(position3);

            // Add scaling animation to the spin button
            startSpinAnimation(spinButton);
        });

        plusButton.setOnClickListener(v -> {
            if (playsound == 1) {
                plumin.start();
            }
            gameLogic.betUp();
            updateText();
        });

        minusButton.setOnClickListener(v -> {
            if (playsound == 1) {
                plumin.start();
            }
            gameLogic.betDown();
            updateText();
        });

        settingsButton.setOnClickListener(v -> {
            if (playsound == 1) {
                plumin.start();
            }
            showSettingsDialog();
        });
    }
    private void startSpinAnimation(View view) {
        Animation scaleAnimation = AnimationUtils.loadAnimation(MagicSlot.this, R.anim.scale_animation);
        view.startAnimation(scaleAnimation);
    }
    private void setText() {
        if (firstRun) {
            gameLogic.setMyCoins(1000);
            gameLogic.setBet(5);
            gameLogic.setJackpot(100000);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();

        } else {
            String coins = pref.getString("coins", "1000");
            String myPlay = pref.getString("play", "5");
            String jackpot = pref.getString("jackpot", "100000");
            Log.d("COINS", coins);
            myCoinsval = Integer.parseInt(coins);
            playval = Integer.parseInt(myPlay);
            fortuneVal = Integer.parseInt(jackpot);
            gameLogic.setMyCoins(myCoinsval);
            gameLogic.setBet(playval);
            gameLogic.setJackpot(fortuneVal);
        }
    }

    private void updateText() {
        energyBallPrice.setText(gameLogic.getJackpot());
        myPower.setText(gameLogic.getMyCoins());
        myPlay.setText(gameLogic.getBet());

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("coins", gameLogic.getMyCoins());
        editor.putString("play", gameLogic.getBet());
        editor.putString("jackpot", gameLogic.getJackpot());
        editor.apply();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        public SpinnerAdapter(MagicSlot magicSlot, int[] slot, Mechanics gameLogic) {
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MagicSlot.this);
            View view = layoutInflater.inflate(R.layout.spin_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % COMBO;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.aladin);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.jamine  );
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.mop);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.genie);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.box);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.bonus);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.magic);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    private void showSettingsDialog() {

        final Dialog dialog;
        ImageButton notifBtn;


        dialog = new Dialog(this, R.style.WinDialog);
        Objects.requireNonNull(dialog.getWindow()).setContentView(R.layout.activity_settings);

        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setCancelable(false);


        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss()); // Close the dialog when the close button is clicked

        ImageButton exit = dialog.findViewById(R.id.btnExit);
        exit.setOnClickListener(v -> finishAffinity());


        musicOn = dialog.findViewById(R.id.music_on);
        musicOn.setOnClickListener(v -> {
            playmusic = 0;
            checkmusic();
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("music", playmusic);
            editor.apply();
        });

        musicOff = dialog.findViewById(R.id.music_off);
        musicOff.setOnClickListener(v -> {
            playmusic = 1;
            bgsound.start();
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("music", playmusic);
            editor.apply();
        });

        soundon = dialog.findViewById(R.id.sounds_on);
        soundon.setOnClickListener(v -> {
            playsound = 0;
            checkmusicdraw();
            soundon.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("sound", playsound);
            editor.apply();
        });

        soundoff = dialog.findViewById(R.id.sounds_off);
        soundoff.setOnClickListener(v -> {
            playsound = 1;
            soundon.setVisibility(View.VISIBLE);
            soundoff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("sound", playsound);
            editor.apply();
        });


        checkmusicdraw();
        checksounddraw();

        dialog.show();
    }



    @Override
    public void onPause() {
        super.onPause();
        bgsound.pause();
    }
    @Override
    public void onResume() {
        super.onResume();
        checkmusic();
    }

    private void checkmusic() {
        if (playmusic == 1) {
            bgsound.start();
        } else {
            bgsound.pause();
        }
    }

    private void checkmusicdraw() {
        if (playmusic == 1) {
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
        } else {
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
        }
    }

    private void checksounddraw() {
        if (playsound == 1) {
            soundon.setVisibility(View.VISIBLE);
            soundoff.setVisibility(View.INVISIBLE);
        } else {
            soundon.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
        }

    }

}