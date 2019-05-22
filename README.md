# Javes discord bot

Javes is a basic discord bot, that can do nothing, but can learn a lot.

Basically I wanted to do something with OpenNLP and as I'm using discord quite
heavily, I've build a simple discord bot.

The bot itself consists of two parts: The CPController (Command Processing Controller)
and the NLPController (Natural Language Processing Controller).

The CPController is the "normal" bot controller, as it's the part of processing old
style commands, like `!ping` or `!echo Hello World`. The command is forwarded to a
corresponding intent, so the correct action is executed.

The NLPController uses OpenNLP to process direct messages to the bot and classifies
their intent. When the corresponding intent is found, the system tries to find entities
in the message, and sends the found data to the corresponding intent, so the correct
action is executed.

So basically it looks like this:

+-----------+      +---------+
|  Message  |----->|  Javes  |
+-----------+      +---------+
              __________|__________
             |                     |
    +----------------+     +-----------------+
    |  CPController  |     |  NLPController  |
    +----------------+     +-----------------+
             |                      |
         +------------+         +--------------+
     +-------------+  |      +--------------+  |
     |  CP-Intent  |--+      |  NLP-Intent  |--+
     +-------------+         +--------------+

## Where does the name come from?

Javes is a mix of *java* and *james*, combinedes to *jav-es*
