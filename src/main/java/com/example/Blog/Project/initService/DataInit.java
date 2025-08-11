package com.example.Blog.Project.initService;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.category.repository.CategoryRepository;
import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.permission.model.PermissionOption;
import com.example.Blog.Project.permission.repository.PermissionRepository;
import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.repository.PostRepository;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
@Profile("!test")
public class DataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;

    private final CategoryRepository categoryRepository;

    private final PostRepository postRepository;

    @Autowired
    public DataInit(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            PermissionRepository permissionRepository,
            CategoryRepository categoryRepository,
            PostRepository postRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initCategories();
        Map<String, Permission> permissionsMap = initPermissions();

        Role roleAdmin = new Role();
        roleAdmin.setName("Admin");
        roleAdmin.setPermissions(new HashSet<>(permissionsMap.values()));
        roleAdmin = roleRepository.save(roleAdmin);

        Role roleUser = new Role();
        roleUser.setName("User");
        roleUser.setPermissions(new HashSet<>(
                List.of(permissionsMap.get(PermissionOption.READ_POSTS.getAbbreviation()), permissionsMap.get(PermissionOption.READ_CATEGORIES.getAbbreviation()))
        ));
        roleRepository.save(roleUser);

        User user = new User();
        user.setUsername("kamkam");
        user.setEmail("kamkam@gmail.com");
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode("Kamkam123!"));
        user.setRole(roleAdmin);

        userRepository.save(user);

        initPosts();
    }

    private Map<String, Permission> initPermissions() {
        Map<String, Permission> permissionMap = new HashMap<>();

        for (PermissionOption permissionOption: PermissionOption.values()) {
            Permission permission = new Permission();
            permission.setTitle(permissionOption.getAbbreviation());
            permission.setDescription(permissionOption.getDescription());
            permission = this.permissionRepository.save(permission);
            permissionMap.put(permissionOption.getAbbreviation(), permission);
        }

        return permissionMap;
    }

    private void initCategories() {
        List<Category> categories = new ArrayList<>();

        Category techAndAlCategory = new Category();
        techAndAlCategory.setTitle("Tech & AI");

        Category scienceAndSpaceCategory = new Category();
        scienceAndSpaceCategory.setTitle("Science & Space");

        Category historyAndCultureCategory = new Category();
        historyAndCultureCategory.setTitle("History & Culture");

        Category businessAndFinanceCategory = new Category();
        businessAndFinanceCategory.setTitle("Business & Finance");

        Category healthAndWellnessCategory = new Category();
        healthAndWellnessCategory.setTitle("Health & Wellness");

        Category selfImprovementCategory = new Category();
        selfImprovementCategory.setTitle("Self-Improvement");

        Category travelAndAdventureCategory = new Category();
        travelAndAdventureCategory.setTitle("Travel & Adventure");

        Category foodAndCookingCategory = new Category();
        foodAndCookingCategory.setTitle("Food & Cooking");

        Category gamingCategory = new Category();
        gamingCategory.setTitle("Gaming");

        Category moviesAndTVShowsCategory = new Category();
        moviesAndTVShowsCategory.setTitle("Movies & TV Shows");

        Category musicAndEntertainmentCategory = new Category();
        musicAndEntertainmentCategory.setTitle("Music & Entertainment");

        Category fashionAndStyleCategory = new Category();
        fashionAndStyleCategory.setTitle("Fashion & Style");

        Category sportsAndFitnessCategory = new Category();
        sportsAndFitnessCategory.setTitle("Sports & Fitness");

        Category socialMediaAndDigitalTrendsCategory = new Category();
        socialMediaAndDigitalTrendsCategory.setTitle("Social Media & Digital Trends");

        Category psychologyAndMindsetCategory = new Category();
        psychologyAndMindsetCategory.setTitle("Psychology & Mindset");

        Category mythologyAndFolkloreCategory = new Category();
        mythologyAndFolkloreCategory.setTitle("Mythology & Folklore");

        categories.add(techAndAlCategory);
        categories.add(scienceAndSpaceCategory);
        categories.add(historyAndCultureCategory);
        categories.add(businessAndFinanceCategory);
        categories.add(healthAndWellnessCategory);
        categories.add(selfImprovementCategory);
        categories.add(travelAndAdventureCategory);
        categories.add(foodAndCookingCategory);
        categories.add(gamingCategory);
        categories.add(moviesAndTVShowsCategory);
        categories.add(musicAndEntertainmentCategory);
        categories.add(fashionAndStyleCategory);
        categories.add(sportsAndFitnessCategory);
        categories.add(psychologyAndMindsetCategory);
        categories.add(mythologyAndFolkloreCategory);
        categories.add(sportsAndFitnessCategory);
        categories.add(socialMediaAndDigitalTrendsCategory);

        categoryRepository.saveAll(categories);
    }

    private void initPosts() {

        List<Post> posts = new ArrayList<>();

        Post techAndAlPost1 = new Post();
        Optional<Category> techAndAICategory = this.categoryRepository.findByTitle("Tech & AI");
        Category category1 = techAndAICategory.get();
        techAndAlPost1.setCategories(Set.of(category1));
        techAndAlPost1.setAuthor("kamkam");
        techAndAlPost1.setDescription("AI is evolving rapidly, and these five tools will transform how we work, communicate, and live.");
        techAndAlPost1.setCreatedAt(Instant.now());
        techAndAlPost1.setTitle("5 AI Tools That Will Change Your Daily Life in 2025");
        techAndAlPost1.setLogo("https://reviewnprep.com/blog/wp-content/uploads/2023/05/AI-Tools-and-Frameworks.jpg");
        techAndAlPost1.setContent("AI is revolutionizing our daily routines, making tasks easier and faster. ChatGPT helps with writing and brainstorming, while Midjourney creates stunning AI-generated images. Notion AI enhances productivity by organizing notes and tasks efficiently. ElevenLabs is making voice generation more realistic than ever. Finally, Perplexity AI acts as a supercharged search engine, delivering quick and accurate answers. These tools are just the beginning of AI’s transformation in our lives.");

        Post techAndAlPost2 = new Post();
        techAndAlPost2.setCategories(Set.of(category1));
        techAndAlPost2.setAuthor("kamkam");
        techAndAlPost2.setTitle("The Future of Chatbots: Will They Replace Human Jobs?");
        techAndAlPost2.setContent("Chatbots are becoming more sophisticated, handling customer service, content creation, and even medical advice. While AI can automate many tasks, it still lacks human creativity and emotional intelligence. Many companies use chatbots for efficiency but still rely on human workers for complex problem-solving. The future may involve collaboration rather than full replacement. However, industries like retail and banking are already seeing significant AI-driven changes. Will chatbots take over completely, or will humans always be needed?");
        techAndAlPost2.setDescription("Chatbots are becoming smarter, but will they replace customer service and creative jobs?");
        techAndAlPost2.setLogo("https://images.prismic.io/intuzwebsite/d9daef05-a416-4e84-b0f8-2d5e2e3b58d8_A+Comprehensive+Guide+to+Building+an+AI+Chatbot%402x.png?w=2400&q=80&auto=format,compress&fm=png8");
        techAndAlPost2.setCreatedAt(Instant.now());

        Post scienceAndSpacePost1 = new Post();
        Optional<Category> scienceAndSpaceCategory = this.categoryRepository.findByTitle("Science & Space");
        Category category2 = scienceAndSpaceCategory.get();
        scienceAndSpacePost1.setCategories(Set.of(category2));
        scienceAndSpacePost1.setAuthor("kamkam");
        scienceAndSpacePost1.setDescription("Scientists have found a planet that could potentially support life. What does this mean for space exploration?");
        scienceAndSpacePost1.setCreatedAt(Instant.now());
        scienceAndSpacePost1.setTitle("NASA’s Latest Discovery: A New Earth-Like Exoplanet?");
        scienceAndSpacePost1.setLogo("https://static.vecteezy.com/system/resources/previews/000/194/904/original/vector-mars-space-mission-badges-and-logo-emblems.jpg");
        scienceAndSpacePost1.setContent("Scientists have discovered a new exoplanet that may have conditions suitable for life. Located in the habitable zone of its star, it has the right temperature for liquid water. NASA's telescopes detected atmospheric signals that hint at possible life-supporting conditions. While it's still too far to explore directly, future space missions could reveal more. Could this be humanity’s next home? Only time and technology will tell.");

        Post scienceAndSpacePost2 = new Post();
        scienceAndSpacePost2.setCategories(Set.of(category2));
        scienceAndSpacePost2.setAuthor("kamkam");
        scienceAndSpacePost2.setDescription("Quantum computers are set to revolutionize medicine, AI, and cybersecurity. Here’s how.");
        scienceAndSpacePost2.setCreatedAt(Instant.now());
        scienceAndSpacePost2.setTitle("Quantum Computing: How It Will Transform the World");
        scienceAndSpacePost2.setLogo("https://static.vecteezy.com/system/resources/previews/022/006/618/large_2x/science-background-illustration-scientific-design-flasks-glass-and-chemistry-physics-elements-generative-ai-photo.jpeg");
        scienceAndSpacePost2.setContent("Quantum computers are set to revolutionize fields like medicine, AI, and cryptography. Unlike traditional computers, they process information at an unimaginable speed. This could lead to breakthroughs in drug discovery and solve problems that take supercomputers years to compute. However, challenges like stability and cost still exist. As companies like Google and IBM invest in quantum research, we might see major advancements within the next decade. The question is, are we ready for this quantum leap?");

        Post historyAndCulturePost1 = new Post();
        Optional<Category> historyAndCultureCategory = this.categoryRepository.findByTitle("History & Culture");
        Category category3 = historyAndCultureCategory.get();
        historyAndCulturePost1.setCategories(Set.of(category3));
        historyAndCulturePost1.setAuthor("kamkam");
        historyAndCulturePost1.setDescription("From lost cities to mysterious artifacts, history still has secrets waiting to be uncovered.");
        historyAndCulturePost1.setCreatedAt(Instant.now());
        historyAndCulturePost1.setTitle("Hidden Secrets of Ancient Civilizations You Never Knew");
        historyAndCulturePost1.setLogo("https://erc.europa.eu/sites/default/files/2022-09/NEW%20WEBSITE%20-%20Events%20-%20image%20header%20(1350%20%C3%97%20810px).jpeg");
        historyAndCulturePost1.setContent("History holds many mysteries, from lost cities to advanced ancient technology. The Antikythera Mechanism, found in a Greek shipwreck, is considered the world’s first analog computer. The pyramids of Egypt still puzzle researchers with their precise construction techniques. Göbekli Tepe, a site older than Stonehenge, challenges our understanding of early civilizations. These discoveries show that ancient people were far more advanced than we once thought. What other secrets are still buried beneath the earth?");

        Post historyAndCulturePost2 = new Post();
        historyAndCulturePost2.setCategories(Set.of(category3));
        historyAndCulturePost2.setAuthor("kamkam");
        historyAndCulturePost2.setDescription("Some cities were once myths—until archaeologists proved them real.");
        historyAndCulturePost2.setCreatedAt(Instant.now());
        historyAndCulturePost2.setTitle("Lost Cities That Were Rediscovered After Centuries");
        historyAndCulturePost2.setLogo("https://nationaltoday.com/wp-content/uploads/2022/09/4568174-min-1200x834.jpg");
        historyAndCulturePost2.setContent("Some cities once thought to be myths turned out to be real. Machu Picchu, hidden in the Andes Mountains, was rediscovered in 1911 after being abandoned for centuries. The ancient city of Pompeii was buried under volcanic ash until explorers uncovered its ruins in the 18th century. Angkor Wat, one of the largest religious monuments in the world, was lost to the jungle before being restored. These cities offer a glimpse into lost civilizations and their incredible achievements. Who knows what other lost worlds are waiting to be found?");

        Post businessAndFinancePost1 = new Post();
        Optional<Category> businessAndFinanceCategory = this.categoryRepository.findByTitle("Business & Finance");
        Category category4 = businessAndFinanceCategory.get();
        businessAndFinancePost1.setCategories(Set.of(category4));
        businessAndFinancePost1.setAuthor("kamkam");
        businessAndFinancePost1.setDescription("Want to make money while you sleep? These strategies can help.");
        businessAndFinancePost1.setCreatedAt(Instant.now());
        businessAndFinancePost1.setTitle("5 Passive Income Ideas That Actually Work in 2025");
        businessAndFinancePost1.setLogo("https://thumbs.dreamstime.com/z/finanse-117953359.jpg");
        businessAndFinancePost1.setContent("Making money while you sleep sounds like a dream, but it’s possible with passive income. Investing in dividend stocks can generate regular payouts. Selling digital products like e-books or courses allows you to earn repeatedly from one-time work. Print-on-demand stores let you sell merchandise without holding inventory. Rental properties provide long-term financial security if managed wisely. The key to success is choosing an income stream that fits your skills and interests.");

        Post businessAndFinancePost2 = new Post();
        businessAndFinancePost2.setCategories(Set.of(category4));
        businessAndFinancePost2.setAuthor("kamkam");
        businessAndFinancePost2.setDescription("If you had $1,000 to invest, where should you put it? Here are smart options.");
        businessAndFinancePost2.setCreatedAt(Instant.now());
        businessAndFinancePost2.setTitle("How to Invest $1,000 for Maximum Returns");
        businessAndFinancePost2.setLogo("https://wallpapers.com/images/hd/business-stocks-professional-uz4x7mtnrljcqupk.jpg");
        businessAndFinancePost2.setContent("If you have $1,000 to invest, where should you put it? The stock market remains a solid option, especially ETFs for diversified growth. Crypto can be risky, but investing in stable assets like Bitcoin could pay off. Real estate crowdfunding lets you invest in properties without buying a house. For safer options, high-yield savings accounts and bonds offer steady returns. The best approach depends on your risk tolerance and long-term goals.");

        Post healthAndWellnessPost1 = new Post();
        Optional<Category> healthAndWellnessCategory = this.categoryRepository.findByTitle("Health & Wellness");
        Category category5 = healthAndWellnessCategory.get();
        healthAndWellnessPost1.setCategories(Set.of(category5));
        healthAndWellnessPost1.setAuthor("kamkam");
        healthAndWellnessPost1.setDescription("Cold showers can boost energy, improve circulation, and even fight depression.");
        healthAndWellnessPost1.setCreatedAt(Instant.now());
        healthAndWellnessPost1.setTitle("The Surprising Benefits of Cold Showers");
        healthAndWellnessPost1.setLogo("https://evokept.com/wp-content/uploads/2015/11/Depositphotos_4672101_original-1024x1024.jpg");
        healthAndWellnessPost1.setContent("Cold showers might seem uncomfortable, but they have amazing benefits for your body and mind. They improve circulation, boost immunity, and even help with muscle recovery. Many people report feeling more alert and energized after just a few minutes under cold water. Studies show that cold exposure can also help fight stress and depression by stimulating endorphin release. While it takes some getting used to, the long-term benefits make it worth trying. Could cold showers be the key to better health?");

        Post healthAndWellnessPost2 = new Post();
        healthAndWellnessPost2.setCategories(Set.of(category5));
        healthAndWellnessPost2.setAuthor("kamkam");
        healthAndWellnessPost2.setDescription("Small actions lead to big changes. Try these five-minute habits.");
        healthAndWellnessPost2.setCreatedAt(Instant.now());
        healthAndWellnessPost2.setTitle("5-Minute Daily Habits That Can Change Your Life");
        healthAndWellnessPost2.setLogo("https://www.trainerize.com/blog/wp-content/uploads/2023/05/BLOG-HEADER-OVERLAYS-1200-X-800-27.png");
        healthAndWellnessPost2.setContent("Small habits can lead to big transformations over time. Starting your day with a gratitude journal can boost your mood and mindset. Taking deep breaths for five minutes reduces stress and improves focus. A quick stretching routine keeps your body flexible and pain-free. Drinking a glass of water in the morning jumpstarts hydration and energy levels. These simple habits require little effort but can make a huge difference in your life.");

        Post selfImprovementPost1 = new Post();
        Optional<Category> selfImprovementCategory = this.categoryRepository.findByTitle("Self-Improvement");
        Category category6 = selfImprovementCategory.get();
        selfImprovementPost1.setCategories(Set.of(category6));
        selfImprovementPost1.setAuthor("kamkam");
        selfImprovementPost1.setDescription("Early risers swear by the 5 AM routine. Here’s why it works.");
        selfImprovementPost1.setCreatedAt(Instant.now());
        selfImprovementPost1.setTitle("Why Waking Up at 5 AM Can Boost Your Productivity");
        selfImprovementPost1.setLogo("https://www.jefit.com/wp/wp-content/uploads/2020/12/young-attractive-brunette-woman-sitting-bed-pajamas-sleeping-mask-smiling-bedroom-happy-emotion-lazy-morning-waking-up-sleepy-sexy-skinny-legs-scaled.jpg");
        selfImprovementPost1.setContent("Many successful people swear by waking up early, but why does it work? Mornings are free from distractions, allowing for focused work and self-care. Studies show that early risers tend to be more disciplined and proactive. It also gives you time to exercise, plan your day, or develop new skills. While adjusting your sleep schedule takes effort, the productivity gains can be life-changing. The question is—are you ready to take the 5 AM challenge?");

        Post selfImprovementPost2 = new Post();
        selfImprovementPost2.setCategories(Set.of(category6));
        selfImprovementPost2.setAuthor("kamkam");
        selfImprovementPost2.setDescription("Reading more isn’t about time—it’s about strategy.");
        selfImprovementPost2.setCreatedAt(Instant.now());
        selfImprovementPost2.setTitle("How to Read 52 Books in a Year (Even If You’re Busy)");
        selfImprovementPost2.setLogo("https://content.thriveglobal.com/wp-content/uploads/2019/08/36631153_l-1080x675.jpg");
        selfImprovementPost2.setContent("Reading one book per week sounds difficult, but it’s all about strategy. Choosing audiobooks allows you to “read” while commuting or doing chores. Speed-reading techniques help you absorb information faster without losing comprehension. Setting small daily goals, like reading 10 pages a day, makes it manageable. Using summaries for non-fiction books can help you get key insights quickly. With the right approach, you can easily reach 52 books a year.");

        Post travelAndAdventurePost1 = new Post();
        Optional<Category> travelAndAdventureCategory = this.categoryRepository.findByTitle("Travel & Adventure");
        Category category7 = travelAndAdventureCategory.get();
        travelAndAdventurePost1.setCategories(Set.of(category7));
        travelAndAdventurePost1.setAuthor("kamkam");
        travelAndAdventurePost1.setDescription("Forget tourist traps—these underrated locations will amaze you.");
        travelAndAdventurePost1.setCreatedAt(Instant.now());
        travelAndAdventurePost1.setTitle("7 Hidden Travel Destinations You Must Visit");
        travelAndAdventurePost1.setLogo("https://assignmentpoint.com/wp-content/uploads/2020/08/traveling.jpg");
        travelAndAdventurePost1.setContent("The world is full of breathtaking places beyond the typical tourist spots. The Faroe Islands offer dramatic cliffs and untouched landscapes. Chefchaouen, Morocco, is a hidden blue-painted city that feels like a dream. Raja Ampat in Indonesia is a paradise for divers with its rich marine life. Albania’s beaches rival Greece’s but with fewer crowds. Kyrgyzstan’s mountain scenery is perfect for adventure seekers. These underrated destinations deserve a spot on your travel list.");

        Post travelAndAdventurePost2 = new Post();
        travelAndAdventurePost2.setCategories(Set.of(category7));
        travelAndAdventurePost2.setAuthor("kamkam");
        travelAndAdventurePost2.setDescription("You don’t need to be rich to see the world—just smart with your money.");
        travelAndAdventurePost2.setCreatedAt(Instant.now());
        travelAndAdventurePost2.setTitle("How to Travel the World on a Budget");
        travelAndAdventurePost2.setLogo("https://wildgreatwall.com/wp-content/uploads/2018/09/How-To-Travel-China-On-A-Budget.jpg");
        travelAndAdventurePost2.setContent("Traveling doesn’t have to be expensive if you plan wisely. Using budget airlines and travel credit card rewards can save money on flights. Staying in hostels, couchsurfing, or renting apartments is cheaper than hotels. Eating like a local instead of dining at touristy restaurants cuts costs. Traveling during off-peak seasons means lower prices and fewer crowds. With the right strategies, you can see the world without breaking the bank.");

        Post foodAndCookingPost1 = new Post();
        Optional<Category> foodAndCookingCategory = this.categoryRepository.findByTitle("Food & Cooking");
        Category category8 = foodAndCookingCategory.get();
        foodAndCookingPost1.setCategories(Set.of(category8));
        foodAndCookingPost1.setAuthor("kamkam");
        foodAndCookingPost1.setDescription("The taste of your coffee depends on more than just beans.");
        foodAndCookingPost1.setCreatedAt(Instant.now());
        foodAndCookingPost1.setTitle("The Science Behind the Perfect Cup of Coffee");
        foodAndCookingPost1.setLogo("https://www.ipohecho.com.my/wp-content/uploads/2023/11/Perak-Coffee-Carnival-at-Polo-Ground-Ipoh-this-Saturday-2-780x470.jpeg");
        foodAndCookingPost1.setContent("Great coffee isn’t just about the beans—it’s a science. The water temperature should be between 195-205°F for optimal extraction. A medium grind works best for drip coffee, while espresso requires a fine grind. The golden coffee-to-water ratio is 1:16 for a balanced flavor. Freshly ground beans always taste better than pre-ground ones. Mastering these details can turn your daily cup into a masterpiece.");

        Post foodAndCookingPost2 = new Post();
        foodAndCookingPost2.setCategories(Set.of(category8));
        foodAndCookingPost2.setAuthor("kamkam");
        foodAndCookingPost2.setDescription("Delicious meals, minimal effort.");
        foodAndCookingPost2.setCreatedAt(Instant.now());
        foodAndCookingPost2.setTitle("10 Easy Recipes You Can Make in Under 20 Minutes");
        foodAndCookingPost2.setLogo("https://www.saltandlavender.com/wp-content/uploads/2018/05/vegan-meal-prep-collage.jpg");
        foodAndCookingPost2.setContent("Cooking doesn’t have to be complicated or time-consuming. Garlic butter shrimp pasta is a quick and delicious dinner option. Avocado toast with poached eggs makes a nutritious breakfast. Stir-fried chicken and veggies can be ready in just minutes. A caprese salad with fresh tomatoes, mozzarella, and basil is a light yet satisfying meal. These fast recipes prove that homemade food can be both easy and tasty.");

        Post gamingPost1 = new Post();
        Optional<Category> gamingCategory = this.categoryRepository.findByTitle("Gaming");
        Category category9 = gamingCategory.get();
        gamingPost1.setCategories(Set.of(category9));
        gamingPost1.setAuthor("kamkam");
        gamingPost1.setDescription("Get ready for next-level graphics and gameplay.");
        gamingPost1.setCreatedAt(Instant.now());
        gamingPost1.setTitle("Upcoming Games in 2025 That Will Blow Your Mind");
        gamingPost1.setLogo("https://wallpapers.com/images/hd/gaming-pictures-dzbquc10pw5k7x6i.jpg");
        gamingPost1.setContent("The gaming industry is evolving rapidly, and 2025 has some exciting releases. Open-world RPGs with hyper-realistic graphics are set to dominate. AI-driven NPCs will make in-game interactions feel more human. VR and AR gaming will become more immersive than ever. Competitive esports titles are also getting major updates. Whether you love action, strategy, or storytelling, there’s something for every gamer next year.");

        Post gamingPost2 = new Post();
        gamingPost2.setCategories(Set.of(category9));
        gamingPost2.setAuthor("kamkam");
        gamingPost2.setDescription("Gaming has come a long way. Here’s the journey.");
        gamingPost2.setCreatedAt(Instant.now());
        gamingPost2.setTitle("The Evolution of Gaming: From Pong to VR Worlds");
        gamingPost2.setLogo("https://img.freepik.com/premium-photo/gaming-room-with-computer-wide-monitor-screen-colorful-neon-lights-setup-esports_148840-31840.jpg");
        gamingPost2.setContent("Gaming has come a long way since its early days. Pong was one of the first video games, with simple pixelated graphics. The 90s introduced 3D worlds with titles like Super Mario 64. Online multiplayer revolutionized gaming with titles like World of Warcraft and Call of Duty. Now, VR and AI-driven games are creating lifelike experiences. The future of gaming looks limitless, with technology pushing boundaries every year.");

        Post moviesAndTVShowsPost1 = new Post();
        Optional<Category> moviesAndTVShowsCategory = this.categoryRepository.findByTitle("Movies & TV Shows");
        Category category10 = moviesAndTVShowsCategory.get();
        moviesAndTVShowsPost1.setCategories(Set.of(category10));
        moviesAndTVShowsPost1.setAuthor("kamkam");
        moviesAndTVShowsPost1.setDescription("These hidden gems deserve more attention.");
        moviesAndTVShowsPost1.setCreatedAt(Instant.now());
        moviesAndTVShowsPost1.setTitle("10 Underrated Movies You Should Watch Right Now");
        moviesAndTVShowsPost1.setLogo("https://staticc.sportskeeda.com/editor/2023/02/c099d-16769375471111-1920.jpg");
        moviesAndTVShowsPost1.setContent("Not all great movies get the attention they deserve. \"The Man from Earth\" is a brilliant sci-fi film with deep philosophical themes. \"A Ghost Story\" is a slow-burn masterpiece about time and loss. \"Coherence\" is a mind-bending thriller that keeps you guessing. \"Hunt for the Wilderpeople\" is a heartwarming comedy with adventure. These hidden gems deserve a place on your watchlist.");

        Post moviesAndTVShowsPost2 = new Post();
        moviesAndTVShowsPost2.setCategories(Set.of(category10));
        moviesAndTVShowsPost2.setAuthor("kamkam");
        moviesAndTVShowsPost2.setDescription("From classics to modern hits, here are the best sci-fi series ever made.");
        moviesAndTVShowsPost2.setCreatedAt(Instant.now());
        moviesAndTVShowsPost2.setTitle("The Best Sci-Fi TV Shows of All Time");
        moviesAndTVShowsPost2.setLogo("https://www.spieltimes.com/wp-content/uploads/2023/05/scifi.jpg");
        moviesAndTVShowsPost2.setContent("Sci-fi shows have shaped pop culture for decades. \"Black Mirror\" explores the dark side of technology. \"Stranger Things\" blends supernatural horror with 80s nostalgia. \"The Expanse\" delivers a realistic take on space politics and exploration. \"Doctor Who\" has been captivating audiences with time travel since the 60s. These shows prove that science fiction is more than just space battles.");

        Post musicAndEntertainmentPost1 = new Post();
        Optional<Category> musicAndEntertainmentCategory = this.categoryRepository.findByTitle("Music & Entertainment");
        Category category11 = musicAndEntertainmentCategory.get();
        musicAndEntertainmentPost1.setCategories(Set.of(category11));
        musicAndEntertainmentPost1.setAuthor("kamkam");
        musicAndEntertainmentPost1.setDescription("Streaming services have changed the way we listen to music and watch movies, making entertainment more accessible than ever. But how does this impact artists and creators? Read more about how streaming is shaping the future of the industry.");
        musicAndEntertainmentPost1.setCreatedAt(Instant.now());
        musicAndEntertainmentPost1.setTitle("The Rise of Streaming Platforms");
        musicAndEntertainmentPost1.setLogo("https://png.pngtree.com/background/20230606/original/pngtree-colorful-music-notes-on-a-black-background-picture-image_2880724.jpg");
        musicAndEntertainmentPost1.setContent("Streaming services have completely transformed the way we consume music and entertainment. Platforms like Spotify, Apple Music, and Netflix provide instant access to millions of songs and movies at our fingertips. This shift has made it easier for independent artists and filmmakers to reach a global audience without relying on major labels or studios. However, it has also raised debates about fair compensation for creators. Despite these challenges, streaming continues to grow, shaping the future of entertainment. As technology advances, we can expect even more interactive and personalized experiences.");

        Post musicAndEntertainmentPost2 = new Post();
        musicAndEntertainmentPost2.setCategories(Set.of(category11));
        musicAndEntertainmentPost2.setAuthor("kamkam");
        musicAndEntertainmentPost2.setDescription("Social media has become a game-changer for musicians, helping them reach global audiences without a record label. Discover how platforms like TikTok and Instagram are transforming the way music is promoted and consumed.");
        musicAndEntertainmentPost2.setCreatedAt(Instant.now());
        musicAndEntertainmentPost2.setTitle("The Impact of Social Media on the Music Industry");
        musicAndEntertainmentPost2.setLogo("https://images.livemint.com/img/2022/09/27/1600x900/cd4cc2c2-3e4c-11ed-beb2-58c7b3916bd4_1664273666951.jpg");
        musicAndEntertainmentPost2.setContent("Social media has revolutionized the music industry, allowing artists to connect with fans like never before. Platforms like TikTok and Instagram have helped new talents go viral overnight, launching successful careers. Artists no longer rely solely on record labels to promote their music; instead, they can market themselves directly. This has also led to new trends, with short-form videos influencing music charts and streaming numbers. While the competition is fierce, the digital era offers endless opportunities for creativity. As social media evolves, its role in shaping the music industry will only grow stronger.");

        Post fashionAndStylePost1 = new Post();
        Optional<Category> fashionAndStyleCategory = this.categoryRepository.findByTitle("Fashion & Style");
        Category category12 = fashionAndStyleCategory.get();
        fashionAndStylePost1.setCategories(Set.of(category12));
        fashionAndStylePost1.setAuthor("kamkam");
        fashionAndStylePost1.setDescription("Fashion is evolving, and 2025 is bringing some exciting trends. From sustainable fashion to smart fabrics, here’s what’s shaping the future of style.");
        fashionAndStylePost1.setCreatedAt(Instant.now());
        fashionAndStylePost1.setTitle("2025 Fashion Trends You Need to Know");
        fashionAndStylePost1.setLogo("https://images.squarespace-cdn.com/content/v1/5655208ae4b0acafe0fb51af/19148af5-8b9f-4714-a6f3-019ce6589fd3/photo-output.jpg");
        fashionAndStylePost1.setContent("Sustainable fashion is no longer a trend—it’s the future. Designers are embracing eco-friendly materials, upcycling, and ethical production methods. Another exciting development is tech-infused clothing, with smart fabrics that adjust to temperature changes or even track health metrics. Oversized silhouettes and bold, futuristic designs will also dominate runways. Meanwhile, vintage-inspired fashion continues to make a comeback, proving that old trends never truly die. Stay ahead of the curve by incorporating these elements into your wardrobe!");

        Post fashionAndStylePost2 = new Post();
        fashionAndStylePost2.setCategories(Set.of(category12));
        fashionAndStylePost2.setAuthor("kamkam");
        fashionAndStylePost2.setDescription("Less is more when it comes to fashion. Learn how to create a minimalist wardrobe that is both stylish and functional.");
        fashionAndStylePost2.setCreatedAt(Instant.now());
        fashionAndStylePost2.setTitle("How to Build a Minimalist Wardrobe That Works");
        fashionAndStylePost2.setLogo("https://assets.vogue.com/photos/6324cbb0563d9de75791b508/master/w_1920,c_limit/___collage_story.jpg");
        fashionAndStylePost2.setContent("A minimalist wardrobe focuses on quality over quantity, making it easier to mix and match outfits effortlessly. Start with must-have essentials like a well-fitted blazer, neutral tops, classic jeans, and versatile shoes. The benefits go beyond style—minimalism saves time, reduces decision fatigue, and promotes sustainability. Sticking to a neutral color palette ensures everything in your wardrobe works together. By investing in timeless pieces rather than chasing fast fashion, you create a wardrobe that lasts.");

        Post sportsAndFitnessPost1 = new Post();
        Optional<Category> sportsAndFitnessCategory = this.categoryRepository.findByTitle("Sports & Fitness");
        Category category13 = sportsAndFitnessCategory.get();
        sportsAndFitnessPost1.setCategories(Set.of(category13));
        sportsAndFitnessPost1.setAuthor("kamkam");
        sportsAndFitnessPost1.setDescription("Want to get in shape but don’t know where to start? Here’s a guide to beginner-friendly workouts, from strength training to cardio.");
        sportsAndFitnessPost1.setCreatedAt(Instant.now());
        sportsAndFitnessPost1.setTitle("The Best Workout Routines for Beginners");
        sportsAndFitnessPost1.setLogo("https://i.pinimg.com/originals/59/8e/59/598e59d5d8a302839b3fa54c8fc3fddd.jpg");
        sportsAndFitnessPost1.setContent("Starting a fitness journey can be overwhelming, but choosing the right routine makes all the difference. Strength training builds muscle and boosts metabolism, while cardio improves endurance and heart health. If you’re unsure where to start, try a mix of both! Home workouts offer convenience, while gym sessions provide equipment and structure. The key is consistency—start small and increase intensity over time. With the right mindset, your fitness journey can be both effective and enjoyable.");

        Post sportsAndFitnessPost2 = new Post();
        sportsAndFitnessPost2.setCategories(Set.of(category13));
        sportsAndFitnessPost2.setAuthor("kamkam");
        sportsAndFitnessPost2.setDescription("Yoga is more than just a series of stretches—it’s a powerful tool for both mental and physical well-being. Find out why it’s worth adding to your routine!");
        sportsAndFitnessPost2.setCreatedAt(Instant.now());
        sportsAndFitnessPost2.setTitle("Why Yoga Is More Than Just Stretching");
        sportsAndFitnessPost2.setLogo("https://121personaltraining.com/wp-content/uploads/2019/12/AdobeStock_278402928-scaled.jpeg");
        sportsAndFitnessPost2.setContent("Yoga isn’t just about flexibility—it’s a complete mind and body workout. It improves strength, balance, and mental clarity while reducing stress and anxiety. Many athletes use yoga to enhance recovery and prevent injuries. Beyond the physical benefits, yoga promotes mindfulness, helping you stay present and focused in daily life. Whether you’re looking to boost mobility or find inner peace, yoga offers something for everyone.");

        Post socialMediaAndDigitalTrendsPost1 = new Post();
        Optional<Category> socialMediaAndDigitalTrendsCategory = this.categoryRepository.findByTitle("Social Media & Digital Trends");
        Category category14 = socialMediaAndDigitalTrendsCategory.get();
        socialMediaAndDigitalTrendsPost1.setCategories(Set.of(category14));
        socialMediaAndDigitalTrendsPost1.setAuthor("kamkam");
        socialMediaAndDigitalTrendsPost1.setDescription("Instagram or TikTok—which is better for creators? We break down the pros and cons to help you decide where to focus your energy.");
        socialMediaAndDigitalTrendsPost1.setCreatedAt(Instant.now());
        socialMediaAndDigitalTrendsPost1.setTitle("Instagram vs. TikTok: Which Platform Is Better for Creators?");
        socialMediaAndDigitalTrendsPost1.setLogo("https://framerusercontent.com/images/C4I6Aedy8ReZmDdcbYMcwDWtJa8.jpg");
        socialMediaAndDigitalTrendsPost1.setContent("Social media creators often face a tough choice: Instagram or TikTok? Instagram offers a polished, curated aesthetic, while TikTok thrives on raw, engaging short videos. TikTok’s algorithm favors rapid growth, making it easier for new creators to go viral. On the other hand, Instagram provides better monetization options and long-term brand-building opportunities. Both platforms have their strengths, and the best choice depends on your content style and goals. So, which one suits your creative journey best?");

        Post socialMediaAndDigitalTrendsPost2 = new Post();
        socialMediaAndDigitalTrendsPost2.setCategories(Set.of(category14));
        socialMediaAndDigitalTrendsPost2.setAuthor("kamkam");
        socialMediaAndDigitalTrendsPost2.setDescription("AI-generated influencers are changing the face of social media. But are they the future of digital marketing, or just a passing trend?");
        socialMediaAndDigitalTrendsPost2.setCreatedAt(Instant.now());
        socialMediaAndDigitalTrendsPost2.setTitle("The Rise of AI-Generated Influencers");
        socialMediaAndDigitalTrendsPost2.setLogo("https://www.hrbartender.com/wp-content/uploads/2017/11/TalentMap-Digital-Trends.png");
        socialMediaAndDigitalTrendsPost2.setContent("AI influencers like Lil Miquela and Imma are gaining massive followings, blurring the lines between real and virtual. These digital personas are created using advanced technology, allowing brands to control every aspect of their image. While they eliminate human flaws, questions about authenticity and ethics arise. Are AI influencers taking opportunities from real creators, or are they simply a new form of entertainment? As technology advances, their presence in marketing will only grow.");

        Post psychologyAndMindsetPost1 = new Post();
        Optional<Category> psychologyAndMindsetCategory = this.categoryRepository.findByTitle("Psychology & Mindset");
        Category category15 = psychologyAndMindsetCategory.get();
        psychologyAndMindsetPost1.setCategories(Set.of(category15));
        psychologyAndMindsetPost1.setAuthor("kamkam");
        psychologyAndMindsetPost1.setDescription("Feeling distracted or unmotivated? A dopamine detox can help you regain focus and take control of your habits. Here’s how it works!");
        psychologyAndMindsetPost1.setCreatedAt(Instant.now());
        psychologyAndMindsetPost1.setTitle("The Power of Dopamine Detox: Reset Your Brain");
        psychologyAndMindsetPost1.setLogo("https://leverageedublog.s3.ap-south-1.amazonaws.com/blog/wp-content/uploads/2020/03/06173108/Psychology_Courses.jpg");
        psychologyAndMindsetPost1.setContent("Modern life bombards us with constant dopamine hits—from social media to binge-watching shows. A dopamine detox involves temporarily cutting out these instant rewards to help your brain reset. By reducing overstimulation, you can improve focus, boost motivation, and regain control over your habits. Simple steps like limiting screen time, avoiding junk food, and practicing mindfulness can make a big difference. It’s not about quitting pleasures forever—it’s about restoring balance.");


        Post psychologyAndMindsetPost2 = new Post();
        psychologyAndMindsetPost2.setCategories(Set.of(category15));
        psychologyAndMindsetPost2.setAuthor("kamkam");
        psychologyAndMindsetPost2.setDescription("We all procrastinate, but why? Learn the psychology behind it and discover practical ways to get things done more efficiently.");
        psychologyAndMindsetPost2.setCreatedAt(Instant.now());
        psychologyAndMindsetPost2.setTitle("Why People Procrastinate (and How to Stop)");
        psychologyAndMindsetPost2.setLogo("https://i0.wp.com/theneurocoaching.academy/wp-content/uploads/2022/06/AdobeStock_409333064-scaled.jpeg");
        psychologyAndMindsetPost2.setContent("Procrastination isn’t just about laziness—it’s often linked to fear, perfectionism, or lack of motivation. Many people delay tasks because they feel overwhelmed or unsure where to start. Breaking tasks into smaller steps can make them feel more manageable. Setting deadlines, using time-blocking techniques, and rewarding progress can also help. The key is to understand why you procrastinate and develop strategies to overcome it.");

        Post mythologyAndFolklorePost1 = new Post();
        Optional<Category> mythologyAndFolkloreCategory = this.categoryRepository.findByTitle("Mythology & Folklore");
        Category category16 = mythologyAndFolkloreCategory.get();
        mythologyAndFolklorePost1.setCategories(Set.of(category16));
        mythologyAndFolklorePost1.setAuthor("kamkam");
        mythologyAndFolklorePost1.setDescription("From Greek gods to Japanese spirits, mythology is full of fascinating stories. Discover some of the most legendary myths from around the world!");
        mythologyAndFolklorePost1.setCreatedAt(Instant.now());
        mythologyAndFolklorePost1.setTitle("The Most Fascinating Myths from Around the World");
        mythologyAndFolklorePost1.setLogo("https://images-cdn.bridgemanimages.com/api/1.0/image/600wm.XXX.35592250.7055475/5226374.jpg");
        mythologyAndFolklorePost1.setContent("Mythology is filled with incredible stories that have shaped cultures for centuries. Greek myths tell of powerful gods and epic heroes, while Norse legends speak of fierce warriors and the end of the world. In Japan, mystical creatures like the Kitsune and Tengu hold deep cultural significance. These myths not only entertain but also teach valuable lessons about morality, fate, and human nature. Their influence can still be seen in modern books, movies, and art.");

        Post mythologyAndFolklorePost2 = new Post();
        mythologyAndFolklorePost2.setCategories(Set.of(category16));
        mythologyAndFolklorePost2.setAuthor("kamkam");
        mythologyAndFolklorePost2.setDescription("Was Atlantis a real lost city or just a myth? Explore the fascinating theories behind one of history’s greatest mysteries!");
        mythologyAndFolklorePost2.setCreatedAt(Instant.now());
        mythologyAndFolklorePost2.setTitle("Did Atlantis Really Exist? Theories Explained");
        mythologyAndFolklorePost2.setLogo("https://mythology.net/wp-content/uploads/2017/04/Atlantis-imagination-380x240.jpg");
        mythologyAndFolklorePost2.setContent("The legend of Atlantis has fascinated historians and conspiracy theorists alike. First mentioned by Plato, Atlantis was described as an advanced civilization that mysteriously disappeared. Some believe it was a real island that sank due to natural disasters, while others think it was purely symbolic. Theories range from lost continents to alien civilizations. Whether fact or fiction, the mystery of Atlantis continues to spark curiosity and debate.");

        posts.add(techAndAlPost1);
        posts.add(techAndAlPost2);
        posts.add(scienceAndSpacePost1);
        posts.add(scienceAndSpacePost2);
        posts.add(historyAndCulturePost1);
        posts.add(historyAndCulturePost2);
        posts.add(businessAndFinancePost1);
        posts.add(businessAndFinancePost2);
        posts.add(healthAndWellnessPost1);
        posts.add(healthAndWellnessPost2);
        posts.add(selfImprovementPost1);
        posts.add(selfImprovementPost2);
        posts.add(travelAndAdventurePost1);
        posts.add(travelAndAdventurePost2);
        posts.add(foodAndCookingPost1);
        posts.add(foodAndCookingPost2);
        posts.add(gamingPost1);
        posts.add(gamingPost2);
        posts.add(moviesAndTVShowsPost1);
        posts.add(moviesAndTVShowsPost2);
        posts.add(musicAndEntertainmentPost1);
        posts.add(musicAndEntertainmentPost2);
        posts.add(fashionAndStylePost1);
        posts.add(fashionAndStylePost2);
        posts.add(sportsAndFitnessPost1);
        posts.add(sportsAndFitnessPost2);
        posts.add(socialMediaAndDigitalTrendsPost1);
        posts.add(socialMediaAndDigitalTrendsPost2);
        posts.add(psychologyAndMindsetPost1);
        posts.add(psychologyAndMindsetPost2);
        posts.add(mythologyAndFolklorePost1);
        posts.add(mythologyAndFolklorePost2);

        this.postRepository.saveAll(posts);
    }
}