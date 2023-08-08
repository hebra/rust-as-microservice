const Hapi = require('@hapi/hapi')
const { addUser } = require('./logic.js')

const init = async () => {

    const server = Hapi.server({
        port: 3000,
        host: 'localhost'
    });

    server.route({
        method: 'POST',
        path: '/api/users',
        handler: async (request, response) => {
            try {
                response.body = await addUser(request.payload)
            } catch (error) {
                throw error
            }
            return response.body
        }
    })

    await server.start();
    console.log('Server running on %s', server.info.uri);
};

process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});

init();
