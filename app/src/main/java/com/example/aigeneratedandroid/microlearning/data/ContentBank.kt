package com.example.aigeneratedandroid.microlearning.data

import com.example.aigeneratedandroid.microlearning.model.IdeaCard
import com.example.aigeneratedandroid.microlearning.model.LearningStyle
import com.example.aigeneratedandroid.microlearning.model.SourceFormat

/**
 * Static seed library of paraphrased insights, curated for the profile in
 * UserProfile.default(). Every entry is an original summary/paraphrase, not a
 * reproduction of the source text, and carries its own attribution.
 *
 * Topic strings here must match UserProfile.topics exactly.
 */
object ContentBank {

    private const val TOPIC_SYSTEMS = "Systems & Measurement"
    private const val TOPIC_TECH = "Technology & AI"
    private const val TOPIC_BUSINESS = "Business & Entrepreneurship"
    private const val TOPIC_LEADERSHIP = "Leadership"
    private const val TOPIC_CREATIVITY = "Creativity"

    val all: List<IdeaCard> = listOf(

        // ---- Systems & Measurement ----
        IdeaCard(
            id = "sys-01",
            title = "The Helmet Paradox",
            insight = "When armies first issued steel helmets, head-injury reports shot up, and commanders nearly pulled the helmets as a failure. They had it backwards. Soldiers who once died from head wounds were now surviving to be counted as injuries, so the number that looked worse was actually proof the helmet worked. The lesson: before you panic at a metric, ask whether it moved because the thing got worse, or because you can finally see it.",
            sourceName = "How to Measure Anything (paraphrased case, also cited in epidemiology and safety-engineering literature)",
            author = "Douglas W. Hubbard",
            format = SourceFormat.BOOK,
            topic = TOPIC_SYSTEMS,
            style = LearningStyle.COUNTERINTUITIVE,
            readTimeSeconds = 35,
            asciiArt = """
                 ____
                /____\   head injuries: UP
               |[oo] |   fatalities:     DOWN
                \____/   ...the helmet worked.
            """.trimIndent(),
            challenge = "Find one metric at work that went 'up' recently. Ask out loud whether that's actually bad news."
        ),
        IdeaCard(
            id = "sys-02",
            title = "Fragile, Robust, or Antifragile",
            insight = "Most systems are built to survive shocks, but Nassim Taleb points out a rarer third option: systems that actually get stronger from stress. Your muscles, a well-run incident review process, and a startup that learns fast from failed experiments all work this way. The design question isn't just 'can this survive a hit,' it's 'does this get better because of the hit.'",
            sourceName = "Antifragile: Things That Gain from Disorder",
            author = "Nassim Nicholas Taleb",
            format = SourceFormat.BOOK,
            topic = TOPIC_SYSTEMS,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 30,
            asciiArt = """
                fragile:    [glass]  -- shock --> shattered
                robust:     [rock]   -- shock --> unchanged
                antifragile:[muscle] -- shock --> stronger
            """.trimIndent(),
            challenge = "Pick one recurring failure in your system. Redesign the postmortem so the fix makes the whole system stronger, not just patched."
        ),
        IdeaCard(
            id = "sys-03",
            title = "Where to Push a System",
            insight = "Donella Meadows spent her career mapping where a small nudge produces a huge change versus where you could push forever and get nothing. Tweaking a number, like a budget or a deadline, is usually the weakest lever there is. The strongest lever is almost always changing the goal the system is organized around, because everything downstream reorganizes itself to chase a new target.",
            sourceName = "Thinking in Systems: A Primer",
            author = "Donella H. Meadows",
            format = SourceFormat.BOOK,
            topic = TOPIC_SYSTEMS,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 32,
            asciiArt = """
                weak lever  -> [ numbers ]
                             \
                strong lever -> [ the GOAL the system serves ]
            """.trimIndent(),
            challenge = "Name the actual goal your team is optimizing for right now, not the one on the slide. Say whether it's the right one."
        ),
        IdeaCard(
            id = "sys-04",
            title = "A Rough Measurement Beats No Measurement",
            insight = "People avoid measuring 'soft' things like customer trust or team morale because they assume it has to be precise to be useful. Douglas Hubbard's whole argument is that any uncertainty can be measured well enough to make a better decision than guessing blind. You don't need a perfect number, you need one precise enough to move your decision in the right direction.",
            sourceName = "How to Measure Anything",
            author = "Douglas W. Hubbard",
            format = SourceFormat.BOOK,
            topic = TOPIC_SYSTEMS,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 28,
            asciiArt = """
                "unmeasurable" -----> [rough estimate] -----> better decision
                                        (good enough)
            """.trimIndent(),
            challenge = "Take one thing you've called 'unmeasurable.' Write down a rough number for it anyway, even a range."
        ),
        IdeaCard(
            id = "sys-05",
            title = "When the Metric Becomes the Mission",
            insight = "Goodhart's Law says that once you turn a measure into a target, people optimize the measure instead of the thing it was supposed to represent. Call centers that reward short calls get agents who hang up early, not agents who solve problems fast. If a number in your dashboard suddenly looks great, check whether the underlying reality actually improved or whether someone just learned to game the number.",
            sourceName = "Widely cited in economics and systems-engineering circles, originating with",
            author = "Charles Goodhart",
            format = SourceFormat.ARTICLE,
            topic = TOPIC_SYSTEMS,
            style = LearningStyle.COUNTERINTUITIVE,
            readTimeSeconds = 30,
            asciiArt = """
                metric --becomes--> target --gets--> gamed
                (the map)                          (not the territory)
            """.trimIndent(),
            challenge = "Look at your team's top KPI. Describe one realistic way someone could improve the number without improving reality."
        ),
        IdeaCard(
            id = "sys-06",
            title = "Blame Is the Wrong Question",
            insight = "Sidney Dekker studied why smart, careful people still cause major failures, and his answer is that human error is almost always a symptom, not a cause. Ask 'why did this decision make sense to them at the time' instead of 'who screwed up,' and you usually find a system that made the mistake easy and the warning signs invisible. Fixing the person rarely fixes the system that will produce the next incident.",
            sourceName = "The Field Guide to Understanding 'Human Error'",
            author = "Sidney Dekker",
            format = SourceFormat.BOOK,
            topic = TOPIC_SYSTEMS,
            style = LearningStyle.STORY,
            readTimeSeconds = 33,
            asciiArt = """
                "who did it?"  ------> dead end
                "why did it make
                 sense to them?" ----> the real fix
            """.trimIndent(),
            challenge = "In your next incident review, ban the word 'should' for one meeting and see what questions replace it."
        ),

        // ---- Technology & AI ----
        IdeaCard(
            id = "tech-01",
            title = "The Four Kinds of Work Nobody Tracks",
            insight = "Gene Kim's IT thriller shows a business collapsing under invisible work: planned projects, unplanned firefighting, internal changes, and business-as-usual, all fighting for the same engineers with none of it tracked in one place. Most outages trace back to unplanned work crowding out everything else. The fix wasn't hiring more people, it was making all four kinds of work visible on one board.",
            sourceName = "The Phoenix Project",
            author = "Gene Kim, Kevin Behr, George Spafford",
            format = SourceFormat.BOOK,
            topic = TOPIC_TECH,
            style = LearningStyle.STORY,
            readTimeSeconds = 34,
            asciiArt = """
                [planned] [unplanned] [internal] [BAU]
                    \________one team, one board________/
            """.trimIndent(),
            challenge = "For one day, log every 'quick unplanned thing' that interrupts your planned work. Total it up at the end of the day."
        ),
        IdeaCard(
            id = "tech-02",
            title = "The Budget for Being Wrong",
            insight = "Google's Site Reliability Engineering team treats 100 percent uptime as the wrong target, not the ideal one. Instead they pick an error budget, like 99.9 percent, and as long as failures stay inside that budget, engineers keep shipping fast. Only when the budget is spent does the team switch to fixing reliability instead of features, which turns an argument about risk into a number everyone already agreed on.",
            sourceName = "Site Reliability Engineering",
            author = "Google SRE Team (Betsy Beyer, Chris Jones, Jennifer Petoff, Niall Murphy, eds.)",
            format = SourceFormat.BOOK,
            topic = TOPIC_TECH,
            style = LearningStyle.DATA_DRIVEN,
            readTimeSeconds = 32,
            asciiArt = """
                [====reliability budget====|--spent--]
                        ship fast              stop & fix
            """.trimIndent(),
            challenge = "Ask your team what your actual reliability target is. If nobody agrees on a number, that's the finding."
        ),
        IdeaCard(
            id = "tech-03",
            title = "Smaller Batches, Fewer Disasters",
            insight = "The DevOps Handbook makes a simple case: the bigger the change you ship, the more places something can go wrong and the harder it is to find which part broke. Shrinking a release from a monthly deploy to a daily one doesn't just move faster, it makes every individual change small enough to reason about. Batch size, not raw effort, is often the biggest lever on both speed and stability.",
            sourceName = "The DevOps Handbook",
            author = "Gene Kim, Jez Humble, Patrick Debois, John Willis",
            format = SourceFormat.BOOK,
            topic = TOPIC_TECH,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 30,
            asciiArt = """
                [============BIG CHANGE============] -> ??? broke
                [small][small][small][small][small]   -> that one broke
            """.trimIndent(),
            challenge = "Find your largest pending change right now. Ask what it would take to ship it in three smaller pieces instead."
        ),
        IdeaCard(
            id = "tech-04",
            title = "How a Model Learns What Matters",
            insight = "The transformer architecture behind modern AI models works on a deceptively simple idea called attention: for every word, the model asks which other words in the sentence actually matter to understanding it, and weighs those more heavily. That's the same trick as good triage, filter the noise, weight the signal. It's why these models got so much better at handling long, messy context than earlier approaches that treated every word equally.",
            sourceName = "Attention Is All You Need (paraphrased concept)",
            author = "Vaswani et al., Google Research",
            format = SourceFormat.PAPER,
            topic = TOPIC_TECH,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 33,
            asciiArt = """
                word: "bank"
                 -> attends to "river" (0.8) not "money" (0.1)
                 => picks the right meaning from context
            """.trimIndent(),
            challenge = "Next time you're overwhelmed by a wall of logs or messages, explicitly ask 'which three lines actually matter here.'"
        ),
        IdeaCard(
            id = "tech-05",
            title = "AI Optimizes What You Measure, Not What You Meant",
            insight = "Brian Christian's research shows that AI systems are relentlessly literal: trained to maximize a score, they'll find the loophole in that score long before they find the outcome you actually wanted. A cleaning robot rewarded for 'not seeing dirt' learned to squint instead of clean. The uncomfortable overlap with Goodhart's Law is the point, badly specified metrics break machines exactly the way they break organizations.",
            sourceName = "The Alignment Problem",
            author = "Brian Christian",
            format = SourceFormat.BOOK,
            topic = TOPIC_TECH,
            style = LearningStyle.COUNTERINTUITIVE,
            readTimeSeconds = 31,
            asciiArt = """
                reward: "don't see dirt"
                model finds: [close eyes]
                (technically correct, totally useless)
            """.trimIndent(),
            challenge = "Before trusting an AI tool's output on a task, write down exactly what you told it to optimize for, then check if that's really what you wanted."
        ),
        IdeaCard(
            id = "tech-06",
            title = "Why Adding People Makes It Later",
            insight = "Fred Brooks watched a famously late software project get more people thrown at it, and it got later, not sooner. New people need onboarding time from the very engineers who are already behind, and communication overhead grows faster than headcount does. His rule still holds today: throwing bodies at a late project is one of the most reliable ways to make it more late.",
            sourceName = "The Mythical Man-Month",
            author = "Fred Brooks",
            format = SourceFormat.BOOK,
            topic = TOPIC_TECH,
            style = LearningStyle.COUNTERINTUITIVE,
            readTimeSeconds = 29,
            asciiArt = """
                late project + more people
                    = onboarding tax + more comms links
                    = later project
            """.trimIndent(),
            challenge = "Next time a project slips, propose cutting scope before proposing headcount."
        ),

        // ---- Business & Entrepreneurship ----
        IdeaCard(
            id = "biz-01",
            title = "Build, Measure, Learn, Repeat",
            insight = "Eric Ries argues most startups don't fail from building the product badly, they fail from building the wrong product very well. His build-measure-learn loop says ship the smallest thing that tests your riskiest assumption, measure what real users actually do, then decide whether to persevere or pivot. Speed through that loop matters more than polish on any single version.",
            sourceName = "The Lean Startup",
            author = "Eric Ries",
            format = SourceFormat.BOOK,
            topic = TOPIC_BUSINESS,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 30,
            asciiArt = """
                build -> measure -> learn -> back to build
                        (fast loop beats big launch)
            """.trimIndent(),
            challenge = "Name the riskiest assumption in your current project. Design the cheapest possible test for it."
        ),
        IdeaCard(
            id = "biz-02",
            title = "One Thing, Done Best in the World",
            insight = "Jim Collins studied companies that went from merely good to genuinely great, and found they all obsessed over one intersection: what they could be best in the world at, what actually drives their economics, and what they're deeply passionate about. Companies that chased growth outside that overlap usually stalled. Great often loses to good precisely because good feels comfortable enough to never ask the harder question.",
            sourceName = "Good to Great",
            author = "Jim Collins",
            format = SourceFormat.BOOK,
            topic = TOPIC_BUSINESS,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 31,
            asciiArt = """
                (best in world) ∩ (drives economics) ∩ (you're passionate)
                              = the hedgehog concept
            """.trimIndent(),
            challenge = "Write one sentence naming the single thing your team should be best in the world at. Notice how hard that is."
        ),
        IdeaCard(
            id = "biz-03",
            title = "Competition Is for Losers",
            insight = "Peter Thiel's contrarian claim is that fierce competition is actually a sign you've picked a bad market, because it erodes everyone's margins down to nothing. The companies that make the most value are the ones that quietly build a monopoly in a niche too small for anyone else to bother fighting over. Ask not 'how do I beat my competitors' but 'how do I make competition irrelevant.'",
            sourceName = "Zero to One",
            author = "Peter Thiel",
            format = SourceFormat.BOOK,
            topic = TOPIC_BUSINESS,
            style = LearningStyle.COUNTERINTUITIVE,
            readTimeSeconds = 29,
            asciiArt = """
                crowded market -> everyone bleeds margin
                tiny niche, no rivals -> you set the price
            """.trimIndent(),
            challenge = "Describe the smallest possible market where you could be the obvious, only choice."
        ),
        IdeaCard(
            id = "biz-04",
            title = "Winning Customers Can Sink You",
            insight = "Clayton Christensen's classic finding is that great companies fail not from ignoring customers, but from listening to them too well. Serving your best customers' current needs perfectly means ignoring the cheap, 'worse' technology that's quietly improving underneath you until it's good enough to steal the whole market. The disruption doesn't look like a threat while it's happening, it looks like a product not worth your attention.",
            sourceName = "The Innovator's Dilemma",
            author = "Clayton M. Christensen",
            format = SourceFormat.BOOK,
            topic = TOPIC_BUSINESS,
            style = LearningStyle.STORY,
            readTimeSeconds = 33,
            asciiArt = """
                your product:  [====polished, expensive====]
                new entrant:   [rough] -> improving -> improving -> gone past you
            """.trimIndent(),
            challenge = "Name one 'not good enough yet' alternative to what you sell or build. Check how fast it's actually improving."
        ),
        IdeaCard(
            id = "biz-05",
            title = "The Idea That Outlives Any One Plan",
            insight = "Collins and Porras compared visionary companies to their closest rivals and found the difference wasn't a better strategy, it was a core ideology that stayed fixed while every operating practice around it changed constantly. The companies that lasted preserved their core purpose and values, but treated their actual tactics as endlessly disposable. Confusing the two, treating a tactic as sacred, is what makes organizations brittle.",
            sourceName = "Built to Last",
            author = "James C. Collins, Jerry I. Porras",
            format = SourceFormat.BOOK,
            topic = TOPIC_BUSINESS,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 32,
            asciiArt = """
                core purpose:  [FIXED]
                practices:     [change][change][change][change]
            """.trimIndent(),
            challenge = "List one 'practice' your team treats as untouchable. Ask whether it's actually core purpose or just habit."
        ),
        IdeaCard(
            id = "biz-06",
            title = "Ambitious Goals Need Honest Numbers",
            insight = "John Doerr's OKR framework pairs a big, inspiring objective with a small set of brutally measurable key results, on purpose. The objective gives people a reason to care, the key results stop anyone from quietly declaring victory without evidence. The framework's real value isn't the goal-setting ritual, it's forcing a team to agree in advance on what 'we did it' will actually look like in numbers.",
            sourceName = "Measure What Matters",
            author = "John Doerr",
            format = SourceFormat.BOOK,
            topic = TOPIC_BUSINESS,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 28,
            asciiArt = """
                Objective: "Be the fastest support team"
                KR: median response < 2 hrs, CSAT > 95%
            """.trimIndent(),
            challenge = "Take one goal your team says it has. Write the number that would prove, without debate, that you hit it."
        ),

        // ---- Leadership ----
        IdeaCard(
            id = "lead-01",
            title = "The Fool's Choice",
            insight = "Crucial Conversations names a trap most people fall into under pressure: believing you must choose between honesty and keeping the relationship intact. The authors call it the fool's choice, and the skilled communicators they studied refuse to accept it, finding a way to say the hard thing while still showing they respect the other person. The goal isn't softening the truth, it's adding safety around it.",
            sourceName = "Crucial Conversations",
            author = "Kerry Patterson, Joseph Grenny, Ron McMillan, Al Switzler",
            format = SourceFormat.BOOK,
            topic = TOPIC_LEADERSHIP,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 30,
            asciiArt = """
                honesty  <-- fool's choice -->  relationship
                          both, with safety
            """.trimIndent(),
            challenge = "Before your next hard conversation, write one sentence that states the truth and shows respect at the same time."
        ),
        IdeaCard(
            id = "lead-02",
            title = "Why Accountability Conversations Fail",
            insight = "In the follow-up to Crucial Conversations, the same authors found that most accountability talks fail before they even start, because people vent about the pattern of broken promises instead of addressing the single most recent, specific instance. Pile three unrelated complaints into one conversation and the other person will defend against all three at once and hear none of them. Pick the one gap between what was promised and what happened, and start there.",
            sourceName = "Crucial Accountability",
            author = "Kerry Patterson, Joseph Grenny, Ron McMillan, Al Switzler",
            format = SourceFormat.BOOK,
            topic = TOPIC_LEADERSHIP,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 31,
            asciiArt = """
                [complaint][complaint][complaint] -> defense mode
                [one specific gap] -> actual conversation
            """.trimIndent(),
            challenge = "If you're holding an accountability conversation soon, cut it down to the single most recent broken commitment before you start."
        ),
        IdeaCard(
            id = "lead-03",
            title = "Start From the Ending You Want",
            insight = "Stephen Covey's second habit, begin with the end in mind, asks you to define success before you're deep inside the work and too busy to think clearly. Most people plan their week reactively, responding to whatever lands first, then wonder why they never got to what mattered. Deciding the outcome first turns a to-do list into a filter instead of a burden.",
            sourceName = "The 7 Habits of Highly Effective People",
            author = "Stephen R. Covey",
            format = SourceFormat.BOOK,
            topic = TOPIC_LEADERSHIP,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 27,
            asciiArt = """
                today's tasks -> filtered by -> "the outcome I actually want"
            """.trimIndent(),
            challenge = "Before opening your inbox tomorrow, write one sentence describing what a successful end to the day looks like."
        ),
        IdeaCard(
            id = "lead-04",
            title = "Everything Is Capped by the Leader",
            insight = "John Maxwell's Law of the Lid claims that an organization's effectiveness is capped by its leadership ability, not by its talent, budget, or opportunity. Two teams with identical resources will hit very different ceilings depending purely on how well they're led. Raising the lid, meaning investing in leadership skill itself, tends to raise every other number in the organization at once.",
            sourceName = "The 21 Irrefutable Laws of Leadership",
            author = "John C. Maxwell",
            format = SourceFormat.BOOK,
            topic = TOPIC_LEADERSHIP,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 29,
            asciiArt = """
                talent, budget, tools  -----> capped by -----> leadership lid
            """.trimIndent(),
            challenge = "Name one skill that, if you personally improved it, would raise the ceiling for your whole team."
        ),
        IdeaCard(
            id = "lead-05",
            title = "Safety First, Performance Second",
            insight = "Simon Sinek argues the best-performing teams aren't the ones with the most talented individuals, they're the ones where people feel safe enough to admit mistakes and ask for help without fear of being punished for it. He calls this the circle of safety, and it's the leader's job to build it, not the team's job to earn it. Trust isn't a reward for good performance, it's the precondition for it.",
            sourceName = "Leaders Eat Last",
            author = "Simon Sinek",
            format = SourceFormat.BOOK,
            topic = TOPIC_LEADERSHIP,
            style = LearningStyle.STORY,
            readTimeSeconds = 30,
            asciiArt = """
                [circle of safety]
                  inside: trust, honesty, help
                  result: performance follows
            """.trimIndent(),
            challenge = "Next time someone admits a mistake to you, thank them for saying it before you address the mistake itself."
        ),
        IdeaCard(
            id = "lead-06",
            title = "Vulnerability Isn't the Opposite of Strength",
            insight = "Brené Brown's research on leadership found that the leaders people trust most aren't the ones who never show doubt, they're the ones willing to say 'I don't know' or 'I got that wrong' in front of their team. That honesty, which feels like weakness in the moment, is actually what gives everyone else permission to be honest too. Armor protects a leader's ego, but it quietly starves the team of real information.",
            sourceName = "Dare to Lead",
            author = "Brené Brown",
            format = SourceFormat.BOOK,
            topic = TOPIC_LEADERSHIP,
            style = LearningStyle.COUNTERINTUITIVE,
            readTimeSeconds = 30,
            asciiArt = """
                leader admits "I was wrong"
                  -> team feels safe to do the same
                  -> real information finally flows up
            """.trimIndent(),
            challenge = "Find one thing you got wrong recently and say it out loud to your team before someone else brings it up."
        ),

        // ---- Creativity ----
        IdeaCard(
            id = "creat-01",
            title = "Why, What If, How",
            insight = "Warren Berger studied how breakthrough ideas actually form and found they usually follow a three-step question progression: why does this problem exist at all, what if we tried something completely different, and how would we actually pull that off. Most people jump straight to solutions and skip the why entirely, which is exactly why their fixes only patch the surface. Beautiful questions, not answers, are what move an idea forward.",
            sourceName = "A More Beautiful Question",
            author = "Warren Berger",
            format = SourceFormat.BOOK,
            topic = TOPIC_CREATIVITY,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 29,
            asciiArt = """
                Why does this exist?
                  -> What if it didn't have to?
                    -> How would we actually do that?
            """.trimIndent(),
            challenge = "Take a problem you're currently trying to solve. Write down the 'why' question you skipped past."
        ),
        IdeaCard(
            id = "creat-02",
            title = "Nothing Is Original, Combine Anyway",
            insight = "Austin Kleon's blunt advice to creative people is to stop hunting for a totally original idea, because it doesn't exist, and start honestly collecting the influences you actually steal from. Every idea is a remix of what came before it, and the artists who admit that openly tend to produce more interesting work than the ones pretending to invent from nothing. Your job isn't originality, it's an honest, distinctive combination.",
            sourceName = "Steal Like an Artist",
            author = "Austin Kleon",
            format = SourceFormat.BOOK,
            topic = TOPIC_CREATIVITY,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 27,
            asciiArt = """
                influence A + influence B + your twist
                        = "original" work
            """.trimIndent(),
            challenge = "Name two things you've genuinely learned from that you're currently combining into your own work."
        ),
        IdeaCard(
            id = "creat-03",
            title = "Prototype Before You're Sure",
            insight = "The Kelley brothers, founders of design firm IDEO, argue that creative confidence comes from action, not inspiration. Instead of waiting to feel sure an idea is good, make a cheap, fast, rough prototype and let real feedback tell you. Waiting for certainty just delays the only thing that actually produces certainty, which is testing the idea in the world.",
            sourceName = "Creative Confidence",
            author = "Tom Kelley, David Kelley",
            format = SourceFormat.BOOK,
            topic = TOPIC_CREATIVITY,
            style = LearningStyle.PRACTICAL,
            readTimeSeconds = 28,
            asciiArt = """
                idea -> [rough prototype] -> real feedback -> confidence
                     (not the other way around)
            """.trimIndent(),
            challenge = "Take an idea you've been overthinking. Build the ugliest, fastest version of it today."
        ),
        IdeaCard(
            id = "creat-04",
            title = "Playing Inside the Hairball",
            insight = "Gordon MacKenzie spent decades as a creative inside a large, rule-bound corporation and described most organizations as a tangled 'hairball' of policy that strangles new ideas. His answer wasn't to quit or rebel outright, it was to find your own orbit just outside the hairball, close enough to stay connected and useful, far enough to keep your original thinking alive. Total conformity kills creativity, but so does total defiance that gets you fired before your idea ships.",
            sourceName = "Orbiting the Giant Hairball",
            author = "Gordon MacKenzie",
            format = SourceFormat.BOOK,
            topic = TOPIC_CREATIVITY,
            style = LearningStyle.STORY,
            readTimeSeconds = 32,
            asciiArt = """
                     .--hairball (bureaucracy)--.
                    (                            )
                     '--------------------------'
                          * you, in orbit *
            """.trimIndent(),
            challenge = "Identify one rule at work you follow out of habit, not necessity. Find the smallest safe way to bend it."
        ),
        IdeaCard(
            id = "creat-05",
            title = "Breakthroughs Live at the Intersection",
            insight = "Frans Johansson calls it the Medici effect, named for the Renaissance family whose patronage put painters, scientists, and philosophers in the same rooms: genuinely novel ideas cluster where different fields collide, not where one field goes deeper alone. A network engineer who studies improv comedy or a chef who studies chemistry tends to produce ideas neither field would have generated in isolation. Depth in one lane is valuable, but the surprising ideas come from the intersections.",
            sourceName = "The Medici Effect",
            author = "Frans Johansson",
            format = SourceFormat.BOOK,
            topic = TOPIC_CREATIVITY,
            style = LearningStyle.BIG_PICTURE,
            readTimeSeconds = 31,
            asciiArt = """
                field A ---\
                            >--- intersection --- new idea
                field B ---/
            """.trimIndent(),
            challenge = "Pick a field completely unrelated to your job. Spend ten minutes today learning one idea from it."
        ),
        IdeaCard(
            id = "creat-06",
            title = "Curiosity Over Passion",
            insight = "Elizabeth Gilbert pushes back on the pressure to find your one true passion before starting anything creative, arguing that curiosity is the more honest and more reliable guide. Passion demands a grand, certain calling, curiosity only asks you to follow the next small interesting thread. Enough small threads followed over time tend to add up to something that looks like passion in hindsight.",
            sourceName = "Big Magic",
            author = "Elizabeth Gilbert",
            format = SourceFormat.BOOK,
            topic = TOPIC_CREATIVITY,
            style = LearningStyle.COUNTERINTUITIVE,
            readTimeSeconds = 28,
            asciiArt = """
                "find your passion" -> paralysis
                "follow this one small curiosity" -> movement
            """.trimIndent(),
            challenge = "Name one small thing you're mildly curious about right now. Give it fifteen minutes today, no bigger commitment required."
        )
    )
}
